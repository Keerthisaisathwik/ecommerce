package com.project.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ecommerce.dto.APIErrorResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.UserRole;
import com.project.ecommerce.exception.GenericIOException;
import com.project.ecommerce.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

        String uri = request.getRequestURI();
        if (uri.startsWith("/auth") || uri.startsWith("/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = requestTokenHeader.split("Bearer ")[1];
            String username = jwtHelper.getUsernameByToken(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Pattern userPattern = Pattern.compile("/user/(\\d+)/");
                Matcher userMatcher = userPattern.matcher(uri);

                Pattern adminPattern = Pattern.compile("/admin/");
                Matcher adminMatcher = adminPattern.matcher(uri);

                if (userMatcher.find()) {
                    Long pathUserId = Long.parseLong(userMatcher.group(1));
                    User user = userRepository.findByUsername(username).orElseThrow();
                    if (!user.getId().equals(pathUserId) && user.getRole() != UserRole.ADMIN) {
                        throw new GenericIOException("You are not authorized to access other " +
                                "users' information");
                    }
                }

                User user = userRepository.findByUsername(username).orElseThrow();

                if (adminMatcher.find() && !(user.getRole() == UserRole.ADMIN)) {
                    throw new GenericIOException("You are not authorized to use this API");
                }

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        } catch (MalformedJwtException | SignatureException | ExpiredJwtException e) {
            printException("Invalid or expired JWT token", response);
            return;
        } catch (Exception e) {
            printException(e.getMessage(), response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void printException(String message, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        APIErrorResponse errorResponse = new APIErrorResponse(message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
