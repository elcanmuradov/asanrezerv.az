package com.rezervet.authservice.service;

import com.rezervet.authservice.entity.User;
import com.rezervet.authservice.exception.NotFoundException;
import com.rezervet.authservice.repository.AuthRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RedisTemplate<String,String> redisTemplate;
    private final AuthRepository authRepository;

    @Value("${spring.security.secret-key}")
    private String SECRET_KEY;

    private static final long REFRESH_TOKEN_TTL_DAYS = 30;

    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .signWith(secretKey())
                .claim("userId",user.getId())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))
                .compact();

    }

    public String generateToken(UUID userId){
        User user = authRepository.findUserById(userId).orElseThrow(()-> new NotFoundException("User not found"));

        return Jwts.builder()
                .subject(user.getEmail())
                .signWith(secretKey())
                .claim("userId",user.getId())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))
                .compact();

    }

    public SecretKey secretKey(){
       return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String findUserNameFromToken(String token){
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(extractToken(token))
                .getPayload()
                .getSubject();
    }

    public boolean tokenControl(String jwt, UserDetails userDetails) {
        final String username = findUserNameFromToken(jwt);
        return (username.equals(userDetails.getUsername()) && !exportToken(jwt, Claims::getExpiration).before(new Date()));


    }

    public long getExpirationTimeFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(extractToken(token))
                .getPayload()
                .getExpiration()
                .getTime();
    }

    public String extractToken(String token) {
        if (token == null) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token.trim();
    }


    public String generateRefreshToken(String email) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "refresh_token:" + refreshToken,
                email,
                REFRESH_TOKEN_TTL_DAYS,
                TimeUnit.DAYS
        );
        return refreshToken;
    }

    private <T> T exportToken(String token, Function<Claims, T> function) {
        final var claims = Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(extractToken(token))
                .getPayload();

        return function.apply(claims);
    }

}
