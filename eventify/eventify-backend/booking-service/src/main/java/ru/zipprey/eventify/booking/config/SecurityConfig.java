package ru.zipprey.eventify.booking.config;

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

@EnableWebSecurity
@Configuration
public class SecurityConfig extends BaseSecurityConfiguration {

    private final InternalApiKeyFilter internalApiKeyFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          InternalApiKeyFilter internalApiKeyFilter) {
        super(jwtAuthenticationFilter);
        this.internalApiKeyFilter = internalApiKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return configureCommon(http)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/internal/**")
                        .hasRole(INTERNAL_SERVICE_ROLE)
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(internalApiKeyFilter, JwtAuthenticationFilter.class)
                .build();
    }
}