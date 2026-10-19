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
import java.util.UUID;

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

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (cacheGateway.exists("blacklist:" + token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("Token revogado. Efetue login novamente.");
            return;
        }

        String subject = tokenProviderGateway.validateToken(token);

        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UUID userId = UUID.fromString(subject);
                userRepositoryGateway.findById(userId).ifPresent(user -> {
                    UserDetailsAdapter userDetails = new UserDetailsAdapter(user);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            } catch (IllegalArgumentException e) {
            }
        }

        filterChain.doFilter(request, response);
    }
}
