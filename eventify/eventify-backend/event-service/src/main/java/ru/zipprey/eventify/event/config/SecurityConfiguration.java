package ru.zipprey.eventify.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import ru.zipprey.eventify.common.configuration.BaseSecurityConfiguration;
import ru.zipprey.eventify.common.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends BaseSecurityConfiguration {

    protected SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        super(jwtAuthenticationFilter);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/events/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );
        return buildFilterChain(http);
    }
}
