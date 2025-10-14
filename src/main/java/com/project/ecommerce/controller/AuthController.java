package com.project.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.ecommerce.dto.APISuccessResponse;
import com.project.ecommerce.dto.LoginRequestDto;
import com.project.ecommerce.dto.LoginResponseDto;
import com.project.ecommerce.dto.SignUpRequestDto;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<APISuccessResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
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
}
