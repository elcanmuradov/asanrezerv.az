package com.rezervet.authservice.service;

import com.rezervet.authservice.dto.UserDto;
import com.rezervet.authservice.dto.auth.request.*;
import com.rezervet.authservice.dto.auth.response.AuthResponse;
import com.rezervet.authservice.dto.auth.response.VerifyOtpCodeResponse;
import com.rezervet.authservice.dto.client.StaffRequest;
import com.rezervet.authservice.entity.User;
import com.rezervet.authservice.exception.AuthException;
import com.rezervet.authservice.exception.NotFoundException;
import com.rezervet.authservice.mapper.UserMapper;
import com.rezervet.authservice.repository.AuthRepository;
import com.rezervet.authservice.utils.enums.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public void sendOtpCode(InitiateRegister request) {

        if (authRepository.existsUserByEmail(request.getEmail())){
            throw new AuthException("Email artıq qeydiyyatdan keçib!");
        }

        String otpCode = generateOtpCode();
        log.info("OTP CODE: {}", otpCode);
        redisTemplate.opsForValue().set(request.getEmail(), otpCode, 5, TimeUnit.MINUTES);


        // notification-service -> send code
    }

    public VerifyOtpCodeResponse verify(VerifyOtpCodeRequest request) {
        String otpCode = redisTemplate.opsForValue().get(request.getEmail());

        if (Optional.ofNullable(otpCode).isEmpty()) {
            throw new NotFoundException("OTP kod tapılmadı!");
        }

        if(!otpCode.equals(request.getOtpCode())){
            throw new AuthException("Yanlış OTP kod!");
        };

        String tempToken = UUID.randomUUID().toString();

        redisTemplate.delete(request.getEmail());
        redisTemplate.opsForValue().set(request.getEmail(), tempToken, 5, TimeUnit.MINUTES);

        return VerifyOtpCodeResponse.builder().email(request.getEmail()).tempToken(tempToken).build();
    }

    public AuthResponse finishRegistration(RegisterRequest request){

        String tempToken = redisTemplate.opsForValue().get(request.getEmail());

        if (Optional.ofNullable(tempToken).isEmpty()){
            throw new NotFoundException("Zəhmət olmasa yenidən cəhd edin!");
        }

        if(!tempToken.equals(request.getTempToken())){
           throw new AuthException("Qeydiyyat üçün müəyyən edilmiş vaxt bitdi. Zəhmət olmasa yenidən cəhd edin!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getAccountType().toRole())
                .fullName(request.getFullName())
                .build();

        user = authRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        return new AuthResponse(jwtToken,refreshToken);

    }



    public AuthResponse login(LoginRequest request) {
        var opt = authRepository.findUserByEmail(request.getEmail());

        if (opt.isEmpty()){
            throw new AuthException("Daxil etdiyiniz email və ya şifrə yanlışdır!");
        }

        User user = opt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new AuthException("Daxil etdiyiniz email və ya şifrə yanlışdır!");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );



        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        return new AuthResponse(token,refreshToken);
    }


    public void logout(String authorizationHeader) {
        long expirationTime = jwtService.getExpirationTimeFromToken(authorizationHeader);
        String token = jwtService.extractToken(authorizationHeader);
        String blacklistKey = "blacklist:token:" + token;

        redisTemplate.opsForValue().set(
                blacklistKey,
                "logout",
                expirationTime,
                TimeUnit.MILLISECONDS
        );
    }


    private String generateOtpCode() {
        return String.valueOf(secureRandom.nextInt(99999, 999999));
    }


    public UserDto getUser(String token) {
        String email = jwtService.findUserNameFromToken(token);
        var opt = authRepository.findUserByEmail(email);
        if (opt.isEmpty()){
            throw new NotFoundException("Istifadəçi tapılmadı!");
        }

        return userMapper.toDto(opt.get());
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshKey = "refresh_token:" + request.getRefreshToken();
        String email = redisTemplate.opsForValue().get(refreshKey);

        if (email == null) {
            throw new AuthException("Sessiyanın vaxtı bitib. Zəhmət olmasa yenidən daxil olun.");
        }

        User user = authRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("İstifadəçi tapılmadı!"));

        // Rotasiya: köhnə refresh token etibarsızlaşdırılır, yenisi verilir
        redisTemplate.delete(refreshKey);

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }



    public UserDto createWaiter(StaffRequest staffRequest) {
        User user = User.builder()
                .email(staffRequest.getEmail())
                .fullName(staffRequest.getFullName())
                .password(passwordEncoder.encode("ofisiant123"))
                .role(Role.WAITER)
                .build();


        user = authRepository.save(user);

        return userMapper.toDto(user);
    }
}
// email -> otp code -> sifre