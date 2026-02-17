package com.medmuncii.medapp;

import com.medmuncii.medapp.auth.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Consider re-enabling CSRF with proper handling for production
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login.html", "/css/**", "/js/**", "/images/**").permitAll() // Public resources
                .requestMatchers("/", "/companies.html", "/employees.html", "/aptitude.html").authenticated() // Protected HTML pages including root
                .requestMatchers("/api/**").authenticated() // All API endpoints protected
                .anyRequest().denyAll() // Deny anything else explicitly to avoid accidental access
            )
            .formLogin(form -> form
                .loginPage("/login.html") // Custom login page
                .loginProcessingUrl("/perform_login") // URL to submit the username and password
                .defaultSuccessUrl("/", true) // Redirect to root on successful login
                .failureUrl("/login.html?error=true") // Redirect to login page on failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/login.html?logout=true")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
