package com.microservice.archchatuserservice.infrastructure.config;

import com.microservice.archchatuserservice.application.gateways.CacheGateway;
import com.microservice.archchatuserservice.application.gateways.TokenProviderGateway;
import com.microservice.archchatuserservice.application.gateways.UserRepositoryGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProviderGateway tokenProviderGateway;
    private final UserRepositoryGateway userRepositoryGateway;
    private final CacheGateway cacheGateway;

    public JwtAuthenticationFilter(TokenProviderGateway tokenProviderGateway, UserRepositoryGateway userRepositoryGateway, CacheGateway cacheGateway) {
        this.tokenProviderGateway = tokenProviderGateway;
        this.userRepositoryGateway = userRepositoryGateway;
        this.cacheGateway = cacheGateway;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        String blacklistKey = "blacklist:" + token;

        if (cacheGateway.exists(blacklistKey)) {
            filterChain.doFilter(request, response);
        }

        String email = tokenProviderGateway.validateToken(token);

        if (email != null){
            userRepositoryGateway.findByEmail(email).ifPresent(user -> {
                UserDetails userDetails = new UserDetailsAdapter(user);

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        filterChain.doFilter(request, response);
    }

}
