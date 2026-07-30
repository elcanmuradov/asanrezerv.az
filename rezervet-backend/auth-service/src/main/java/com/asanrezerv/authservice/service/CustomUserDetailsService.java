package com.asanrezerv.authservice.service;
import com.asanrezerv.authservice.entity.User;
import com.asanrezerv.authservice.exception.NotFoundException;
import com.asanrezerv.authservice.repository.AuthRepository;
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