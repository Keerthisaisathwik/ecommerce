package com.project.ecommerce.security;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.UserRole;
import com.project.ecommerce.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("incoming request: {}" + request.getRequestURI());

        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = requestTokenHeader.split("Bearer ")[1];
        String username = jwtHelper.getUsernameByToken(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            String uri = request.getRequestURI();
            Pattern userPattern = Pattern.compile("/user/(\\d+)/");
            Matcher userMatcher = userPattern.matcher(uri);

            Pattern adminPattern = Pattern.compile("/admin/");
            Matcher adminMatcher = adminPattern.matcher(uri);

            if(userMatcher.find()){
                Long pathUserId = Long.parseLong(userMatcher.group(1));
                User user = userRepository.findByUsername(username).orElseThrow();
                if (!user.getId().equals(pathUserId) && user.getRole() != UserRole.ADMIN) {
                    throw new IOException("You are not authorized to access other users' information");
                }
            }

            User user = userRepository.findByUsername(username).orElseThrow();

            if(adminMatcher.find() && !(user.getRole()== UserRole.ADMIN)){
                throw new IOException("You are not authorized to use this API");
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
