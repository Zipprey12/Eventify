package ru.zipprey.eventify.notification.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import ru.zipprey.eventify.common.configuration.BaseSecurityConfiguration;
import ru.zipprey.eventify.common.security.JwtAuthenticationFilter;
import ru.zipprey.eventify.internalsecurity.InternalApiKeyFilter;

import static ru.zipprey.eventify.internalsecurity.InternalServiceKeysProperties.INTERNAL_SERVICE_ROLE;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends BaseSecurityConfiguration {

    private final InternalApiKeyFilter internalApiKeyFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          InternalApiKeyFilter internalApiKeyFilter) {
        super(jwtAuthenticationFilter);
        this.internalApiKeyFilter = internalApiKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/user/notifications/confirm-email").permitAll()
                .requestMatchers("/internal/telegram/**").hasRole(INTERNAL_SERVICE_ROLE)
                .anyRequest().authenticated()
        );
        return configureCommon(http)
                .addFilterBefore(internalApiKeyFilter, JwtAuthenticationFilter.class)
                .build();
    }
}