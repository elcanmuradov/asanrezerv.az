package com.asanrezerv.authservice.repository;

import com.asanrezerv.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByEmail(String email);

    boolean existsUserByEmail(String email);

    Optional<User> findUserById(UUID id);
}
