
package com.example.project.management.config;

import com.example.project.management.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // =========================
    // PASSWORD ENCODER
    // =========================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================
    // SECURITY FILTER CHAIN
    // =========================

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                 
                 .cors(cors-> {})
                // Disable CSRF for REST API
                .csrf(csrf -> csrf.disable())

                // JWT authentication is stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =========================
                // AUTHORIZATION
                // =========================

                .authorizeHttpRequests(auth -> {

                    // =========================
                    // AUTH
                    // =========================

                    // Login does not require JWT
                    auth.requestMatchers(
                            "/api/auth/**"
                    ).permitAll();


                    // =========================
                    // PROJECTS
                    // =========================

                    // Manager + Employee can VIEW projects
                    auth.requestMatchers(
                            HttpMethod.GET,
                            "/api/projects/**"
                    ).hasAnyRole("MANAGER", "EMPLOYEE");

                    // Only Manager can CREATE projects
                    auth.requestMatchers(
                            HttpMethod.POST,
                            "/api/projects/**"
                    ).hasRole("MANAGER");

                    // Only Manager can UPDATE projects
                    auth.requestMatchers(
                            HttpMethod.PUT,
                            "/api/projects/**"
                    ).hasRole("MANAGER");

                    // Only Manager can DELETE projects
                    auth.requestMatchers(
                            HttpMethod.DELETE,
                            "/api/projects/**"
                    ).hasRole("MANAGER");


                    // =========================
                    // TASKS
                    // =========================

                    // Manager + Employee can VIEW tasks
                    auth.requestMatchers(
                            HttpMethod.GET,
                            "/api/tasks/**"
                    ).hasAnyRole("MANAGER", "EMPLOYEE");

                    // Only Manager can CREATE tasks
                    auth.requestMatchers(
                            HttpMethod.POST,
                            "/api/tasks/**"
                    ).hasRole("MANAGER");

                    // Only Manager can UPDATE tasks
                    auth.requestMatchers(
        HttpMethod.PUT,
        "/api/tasks/**"
).hasAnyRole("MANAGER", "EMPLOYEE");

                    // Only Manager can DELETE tasks
                    auth.requestMatchers(
                            HttpMethod.DELETE,
                            "/api/tasks/**"
                    ).hasRole("MANAGER");


                    // =========================
                    // USERS
                    // =========================

                    // Only Manager can VIEW users
                    auth.requestMatchers(
                            HttpMethod.GET,
                            "/api/users/**"
                    ).hasRole("MANAGER");

                    // Only Manager can CREATE users
                    auth.requestMatchers(
                            HttpMethod.POST,
                            "/api/users/**"
                    ).hasRole("MANAGER");

                    // Only Manager can UPDATE users
                    auth.requestMatchers(
                            HttpMethod.PUT,
                            "/api/users/**"
                    ).hasRole("MANAGER");

                    // Only Manager can DELETE users
                    auth.requestMatchers(
                            HttpMethod.DELETE,
                            "/api/users/**"
                    ).hasRole("MANAGER");
                    auth.requestMatchers(HttpMethod.GET, "/api/dashboard")
        .hasAnyRole("MANAGER", "EMPLOYEE");


                    // =========================
                    // EVERYTHING ELSE
                    // =========================

                    // Any other endpoint requires login
                    auth.anyRequest().authenticated();
                })

                // =========================
                // JWT FILTER
                // =========================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

