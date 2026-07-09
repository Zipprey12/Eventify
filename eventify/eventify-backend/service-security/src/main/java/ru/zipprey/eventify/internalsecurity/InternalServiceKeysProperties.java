package ru.zipprey.eventify.internalsecurity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "internal")
public class InternalServiceKeysProperties {

    public static final String HEADER_NAME = "X-Internal-Api-Key";
    public static final String INTERNAL_SERVICE_AUTHORITY = "ROLE_INTERNAL_SERVICE";
    public static final String INTERNAL_SERVICE_ROLE = "INTERNAL_SERVICE";

    private Map<String, String> trustedServices = new HashMap<>();
}
