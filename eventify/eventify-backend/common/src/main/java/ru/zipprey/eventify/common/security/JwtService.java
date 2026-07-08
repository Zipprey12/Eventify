package ru.zipprey.eventify.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    public static final String TOKEN_NOT_EXISTS = """
            JWT_SECRET не задан - используется временный ключ.
            Все токены будут аннулированы при перезапуске приложения!
            """;

    public static final String ROLE_CLAIM = "role";

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret:#{null}}") String secret,
                      @Value("${jwt.expiration}") long expirationMs) {
        if (secret == null) {
            secret = generateKey();
            log.warn(TOKEN_NOT_EXISTS);
        }
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String email, String role) {
        var now = new Date();
        var expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .claim(ROLE_CLAIM, role)
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get(ROLE_CLAIM, String.class);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String generateKey() {
        return Encoders.BASE64.encode(
                Jwts.SIG.HS256.key().build().getEncoded()
        );
    }
}
