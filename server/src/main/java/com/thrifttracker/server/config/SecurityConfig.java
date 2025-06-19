package com.thrifttracker.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This class holds the security configuration for the application.
 */
@Configuration // Tells Spring that this class contains configuration beans.
@EnableWebSecurity // This turns on Spring's web security features.
public class SecurityConfig {

    /**
     * This method creates and configures the PasswordEncoder bean.
     * The @Bean annotation tells Spring to manage this object.
     * This is the exact bean that Spring was looking for and could not find.
     * We can now inject this PasswordEncoder anywhere we need it.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This method defines our security rules for all HTTP requests.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // For stateless REST APIs, we disable CSRF protection.
                .csrf(csrf -> csrf.disable())
                // This section defines which requests need authentication.
                .authorizeHttpRequests(auth -> auth
                        // We permit ALL requests to /api/auth/** so users can register and log in.
                        .requestMatchers("/api/auth/**").permitAll()
                        // Any other request must be authenticated.
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}