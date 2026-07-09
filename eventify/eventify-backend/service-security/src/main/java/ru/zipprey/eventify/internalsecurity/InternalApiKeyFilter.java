package ru.zipprey.eventify.internalsecurity;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static ru.zipprey.eventify.internalsecurity.InternalServiceKeysProperties.HEADER_NAME;
import static ru.zipprey.eventify.internalsecurity.InternalServiceKeysProperties.INTERNAL_SERVICE_AUTHORITY;

@Component
@RequiredArgsConstructor
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private final InternalServiceKeysProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var providedKey = request.getHeader(HEADER_NAME);

        if (providedKey != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            properties.getTrustedServices().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(providedKey))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .ifPresent(callerName -> {
                        var authorities = List.of(new SimpleGrantedAuthority(INTERNAL_SERVICE_AUTHORITY));
                        var authToken = new UsernamePasswordAuthenticationToken(callerName, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    });
        }
        filterChain.doFilter(request, response);
    }
}