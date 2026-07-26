package com.rezervet.authservice.service;
import com.rezervet.authservice.entity.User;
import com.rezervet.authservice.exception.NotFoundException;
import com.rezervet.authservice.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;
    @Override
    public UserDetails loadUserByUsername(String username){
        Optional<User> user = authRepository.findUserByEmail(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User not found with username: " + username);
        }
    }
}