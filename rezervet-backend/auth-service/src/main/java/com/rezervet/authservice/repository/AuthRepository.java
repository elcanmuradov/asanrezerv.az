package com.rezervet.authservice.repository;

import com.rezervet.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByEmail(String email);

    boolean existsUserByEmail(String email);

    Optional<User> findUserById(UUID id);
}
