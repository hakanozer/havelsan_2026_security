// SecurityConfig.java
package com.works.config;

import com.works.service.CustomerService;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiterRegistry rateLimiterRegistry;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/product/**").hasRole("product")
                        .requestMatchers("/note/**").hasRole("note")
                        .requestMatchers("/customer/**").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterAfter(
                        new RateLimitFilter(rateLimiterRegistry),
                        UsernamePasswordAuthenticationFilter.class
                )
                .formLogin(login -> login.disable())
                .csrf(csrf -> csrf.disable())
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customerService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}