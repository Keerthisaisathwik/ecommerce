package com.project.ecommerce.security;

import com.project.ecommerce.dto.LoginRequestDto;
import com.project.ecommerce.dto.LoginResponseDto;
import com.project.ecommerce.dto.SignUpRequestDto;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserDetails;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.UserDetailsRepository;
import com.project.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
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

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
        User user = (User) authentication.getPrincipal();
        String token = jwtHelper.generateAccessToken(user);
        UserDetails userDetails = userDetailsRepository.findByUser(user).orElseThrow();
//        List<String> roles =
//                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        List<String> roles = new ArrayList<>(
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
        roles.add("CUSTOMER");
        return new LoginResponseDto(token, user.getId(), roles.get(0),
                userDetails.getFirstName());
    }

    public void signup(SignUpRequestDto signUpRequestDto) throws GenericException {
        User user = userRepository.findByUsername(signUpRequestDto.getUsername()).orElse(null);
        if (user != null) {
            throw new GenericException("Username already exists");
        }
        user = userRepository.save(User.builder()
                .username(signUpRequestDto.getUsername())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .build()
        );
        userDetailsRepository.save(UserDetails.builder()
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

    }
}
