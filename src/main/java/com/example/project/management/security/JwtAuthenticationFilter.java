package com.example.project.management.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        // No token
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authHeader.substring(7);

        try {

            Claims claims =
                    jwtService.validateToken(token);

            // =========================
            // JWT DEBUG
            // =========================

            System.out.println("===== JWT DEBUG =====");
            System.out.println(
                    "Request URL: " +
                    request.getRequestURI()
            );
            System.out.println(
                    "Email: " +
                    claims.getSubject()
            );
            System.out.println(
                    "Role from JWT: " +
                    claims.get("role")
            );
            System.out.println("=====================");

            String email =
                    claims.getSubject();

            String role =
                    claims.get("role", String.class);

            if (email == null || role == null) {

                throw new RuntimeException(
                        "Invalid token claims"
                );
            }

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {

            System.out.println(
                    "===== JWT ERROR ====="
            );

            System.out.println(
                    "Request URL: " +
                    request.getRequestURI()
            );

            System.out.println(
                    "Error: " +
                    e.getMessage()
            );

            System.out.println(
                    "====================="
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"status\":401,\"message\":\"Invalid or expired token\"}"
            );
        }
    }
}