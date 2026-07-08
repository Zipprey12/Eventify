package ru.zipprey.eventify.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import ru.zipprey.eventify.common.configuration.BaseSecurityConfiguration;
import ru.zipprey.eventify.common.security.JwtAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends BaseSecurityConfiguration {

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        super(jwtAuthenticationFilter);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
        );
        return buildFilterChain(http);
    }
}
