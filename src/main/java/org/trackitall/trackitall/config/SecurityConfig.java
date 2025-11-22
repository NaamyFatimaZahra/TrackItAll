package org.trackitall.trackitall.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.trackitall.trackitall.common.security.AccountService;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccountService accountService;
    private final AccessDeniedHandler accessDeniedHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.userDetailsService(accountService); // Load user from DB

        http.httpBasic(httpBasic -> httpBasic
                .realmName("TrackItAll API")
        );

        http.exceptionHandling(exception -> exception
                .accessDeniedHandler(accessDeniedHandler)
        );

        return http.build();
    }
}
