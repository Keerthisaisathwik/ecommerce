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
        return new ResponseEntity<>(APISuccessResponse.<LoginResponseDto>builder().data(authService.login(loginRequestDto)).status(true).message(null).build(), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<APISuccessResponse> signup(@RequestBody SignUpRequestDto signUpRequestDto) throws GenericException {
        authService.signup(signUpRequestDto);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).status(true).message(null).build(),
                HttpStatus.CREATED);
    }

    @GetMapping("/login-by-code")
    public ResponseEntity<APISuccessResponse<LoginResponseDto>> loginByAuthCode(@RequestParam String authcode) {
        try {
            LoginResponseDto loginResponse = authService.consumeLoginResponse(authcode);

            if (loginResponse == null) {
                return new ResponseEntity<>(
                        APISuccessResponse.<LoginResponseDto>builder()
                                .data(null)
                                .status(false)
                                .message("Invalid or expired auth code")
                                .build(),
                        HttpStatus.BAD_REQUEST
                );
            }

            return new ResponseEntity<>(
                    APISuccessResponse.<LoginResponseDto>builder()
                            .data(loginResponse)
                            .status(true)
                            .message(null)
                            .build(),
                    HttpStatus.OK
            );
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(
                    APISuccessResponse.<LoginResponseDto>builder()
                            .data(null)
                            .status(false)
                            .message("Server error while processing login")
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
