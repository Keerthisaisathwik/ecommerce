package com.project.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.ecommerce.dto.*;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserDetails;
import com.project.ecommerce.exception.EmailVerificationException;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.security.AuthService;
import com.project.ecommerce.security.JwtHelper;
import com.project.ecommerce.service.UserDetailsService;
import com.project.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtHelper jwtHelper;

    private final UserDetailsService userDetailsService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<APISuccessResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) throws EmailVerificationException {
        return new ResponseEntity<>(APISuccessResponse.<LoginResponseDto>builder().data(authService.login(loginRequestDto)).build(), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<APISuccessResponse> signup(@RequestBody SignUpRequestDto signUpRequestDto) throws GenericException {
        authService.signup(signUpRequestDto);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(),
                HttpStatus.CREATED);
    }

    @GetMapping("/login-by-code")
    public ResponseEntity<APISuccessResponse<LoginResponseDto>> loginByAuthCode(@RequestParam String authcode) throws GenericException {
        try {
            LoginResponseDto loginResponse = authService.consumeLoginResponse(authcode);

            if (loginResponse == null) {
                throw new GenericException("Invalid or expired auth code");
            }

            return new ResponseEntity<>(
                    APISuccessResponse.<LoginResponseDto>builder()
                            .data(loginResponse)
                            .build(),
                    HttpStatus.OK
            );
        } catch (JsonProcessingException e) {
            throw new GenericException("Server error while processing login");
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<APISuccessResponse> verifyEmail(@RequestBody VerifyEmailDTO verifyEmailDTO) throws EmailVerificationException,
            GenericException{
        String emailString = jwtHelper.extractEmail(verifyEmailDTO.getToken());
        UserDetails userDetails = userDetailsService.findByEmail(emailString).orElse(null);
        User user = userDetails.getUser();
        if(userDetails == null){
            throw new GenericException("No account is registered with this email. Click to sign " +
                    "up.");
        }

        if (user == null || user.getVerificationToken() == null) {
            throw new EmailVerificationException("Token Expired!");
        }

        if (!jwtHelper.validateToken(verifyEmailDTO.getToken()) || !user.getVerificationToken().equals(verifyEmailDTO.getToken())) {
            throw new EmailVerificationException("Token Expired!");
        }
        user.setVerificationToken(null);
        user.setIsVerified(true);
        user.setUsername(verifyEmailDTO.getUsername());
        user.setPassword(passwordEncoder.encode(verifyEmailDTO.getPassword()));
        userService.save(user);

        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }
}
