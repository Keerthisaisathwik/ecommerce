package com.project.ecommerce.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ecommerce.config.AppCofig;
import com.project.ecommerce.dto.LoginRequestDto;
import com.project.ecommerce.dto.LoginResponseDto;
import com.project.ecommerce.dto.SignUpRequestDto;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserDetails;
import com.project.ecommerce.enums.AuthProviderType;
import com.project.ecommerce.enums.UserRole;
import com.project.ecommerce.exception.EmailVerificationException;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.UserDetailsRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final UserDetailsRepository userDetailsRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtHelper jwtHelper;

    private final AppCofig appCofig;

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    private static final int TTL_SECONDS = 300; // 5 min

    private final EmailService emailService;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws EmailVerificationException {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
        User user = (User) authentication.getPrincipal();
        String token = jwtHelper.generateAccessToken(user);
        UserDetails userDetails = userDetailsRepository.findByUser(user).orElseThrow();
        if (!user.getIsVerified()) {
            user.setVerificationToken(generateCode());
            userRepository.save(user);
            emailService.sendVerificationEmail(userDetails.getEmail(), user.getVerificationToken());
            throw new EmailVerificationException("Verification email resent. Please check your " +
                    "inbox.");
        }
        return new LoginResponseDto(token, user.getId(), user.getRole(),
                userDetails.getFirstName());
    }

    public void signup(SignUpRequestDto signUpRequestDto) throws GenericException {
        User user = userRepository.findByUsername(signUpRequestDto.getEmail()).orElse(null);
        if (user != null) {
            throw new GenericException("Email already exists");
        }
        user = userRepository.save(User.builder()
                .username(signUpRequestDto.getEmail())
                .password(null)
                .role(UserRole.CUSTOMER)
                .providerType(AuthProviderType.EMAIL)
                .build()
        );
        UserDetails userDetails = userDetailsRepository.save(UserDetails.builder()
                .title(signUpRequestDto.getTitle())
                .user(user)
                .firstName(signUpRequestDto.getFirstName())
                .lastName(signUpRequestDto.getLastName())
                .phoneNumber(signUpRequestDto.getPhoneNumber())
                .email(signUpRequestDto.getEmail())
                .address(signUpRequestDto.getAddress())
                .pincode(signUpRequestDto.getPincode())
                .build()
        );
        user.setVerificationToken(jwtHelper.generateEmailToken(signUpRequestDto.getEmail()));
        userRepository.save(user);
        emailService.sendVerificationEmail(userDetails.getEmail(), user.getVerificationToken());
    }

    // Generate 6-digit numeric code
    public String generateCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(1_000_000);
        return String.format("%06d", number);
    }

    // Store auth code and loginResponse in Redis
    public String storeLoginResponse(LoginResponseDto loginResponse) throws JsonProcessingException {
        String code = generateCode();
        String key = "authcode:" + code;

        // Serialize loginResponse to JSON and store in Redis
        String value = objectMapper.writeValueAsString(loginResponse);
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(TTL_SECONDS));
        return code;
    }

    // Verify auth code and return LoginResponseDto
    public LoginResponseDto consumeLoginResponse(String code) throws JsonProcessingException {
        String key = "authcode:" + code;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null; // invalid or expired
        }
        // Delete after retrieving (one-time use)
        redisTemplate.delete(key);
        return objectMapper.readValue(value, LoginResponseDto.class);
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User,
                                                                     String registrationId) {
        AuthProviderType providerType =
                appCofig.getProviderTypeFromRegistrationId(registrationId);

        String providerId = appCofig.determineProviderIdOAuth2User(oAuth2User, registrationId);
        User user = userRepository.findByProviderTypeAndProviderId(providerType, providerId);
        String email = oAuth2User.getAttribute("email");
        User emailUser = userRepository.findByUsername(email).orElse(null);
        String name = oAuth2User.getAttribute("name");
        if (user == null && emailUser == null) {
            user = userRepository.save(User.builder()
                    .username(oAuth2User.getAttribute("email"))
                    .providerId(providerId)
                    .providerType(providerType)
                    .build()
            );
            userDetailsRepository.save(UserDetails.builder()
                    .firstName(oAuth2User.getAttribute("name"))
                    .email(oAuth2User.getAttribute("email"))
                    .user(user)
                    .build()
            );
        } else if (user == null && emailUser != null) {
            throw new IllegalArgumentException("User already exists with that email");
        }
        String token = jwtHelper.generateAccessToken(user);
        LoginResponseDto loginResponseDto = new LoginResponseDto(token, user.getId(),
                user.getRole(),
                name != null ? name : email.substring(0, email.length() > 15 ? email.length() - 10 :
                        email.length()));
        return ResponseEntity.ok(loginResponseDto);
    }
}
