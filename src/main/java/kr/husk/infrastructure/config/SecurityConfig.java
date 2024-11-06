package kr.husk.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests.requestMatchers(
                                        "/auth/send-code",
                                        "/auth/verify-code",
                                        "/auth/sign-up",
                                        "/swagger-resources/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/webjars/**",
                                        "/error").permitAll()
                                .anyRequest().authenticated());

        return httpSecurity.build();
    }
}
