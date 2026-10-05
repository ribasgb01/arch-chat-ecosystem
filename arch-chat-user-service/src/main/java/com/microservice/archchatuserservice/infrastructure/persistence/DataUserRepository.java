package com.microservice.archchatuserservice.infrastructure.persistence;

import com.microservice.archchatuserservice.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByNickname(String nickname);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.nickname) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "u.isEmailVerified = true")
    List<UserEntity> searchUsers(@Param("query") String query);
}
