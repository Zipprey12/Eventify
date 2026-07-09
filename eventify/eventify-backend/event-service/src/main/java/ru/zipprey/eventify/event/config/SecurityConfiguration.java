package ru.zipprey.eventify.event.config;

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
public class SecurityConfiguration extends BaseSecurityConfiguration {

    private final InternalApiKeyFilter internalApiKeyFilter;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
                                 InternalApiKeyFilter internalApiKeyFilter) {
        super(jwtAuthenticationFilter);
        this.internalApiKeyFilter = internalApiKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return configureCommon(http)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/events/batch").hasRole(INTERNAL_SERVICE_ROLE)
                        .requestMatchers(HttpMethod.PUT, "/events/*/book").hasRole(INTERNAL_SERVICE_ROLE)
                        .requestMatchers(HttpMethod.PUT, "/events/*/free").hasRole(INTERNAL_SERVICE_ROLE)
                        .requestMatchers(HttpMethod.GET, "/events/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(internalApiKeyFilter, JwtAuthenticationFilter.class)
                .build();
    }
}
