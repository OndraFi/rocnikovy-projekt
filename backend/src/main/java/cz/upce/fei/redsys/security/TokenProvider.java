package cz.upce.fei.redsys.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class TokenProvider {

    @Value("${security.jwt.secret:}")
    private String secret;

    private static final String ISSUER = "SemA";
    private static final long EXPIRY_SECONDS = 900L; // 15 minutes

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(EXPIRY_SECONDS);
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("username", username)
                .claim("authorities", authorities)
                .signWith(getSecretKey())
                .compact();
    }

    public Authentication toAuthentication(String token) {
        Claims claims = parseAndValidate(token);
        String username = claims.getSubject();
        List<?> authoritiesRaw = claims.get("authorities", List.class);
        List<SimpleGrantedAuthority> grantedAuthorities = authoritiesRaw == null
                ? List.of()
                : authoritiesRaw.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
    }

    public boolean isValid(String token) {
        try {
            parseAndValidate(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims parseAndValidate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date exp = claims.getExpiration();
        if (exp == null || exp.before(Date.from(Instant.now()))) {
            throw new JwtException("Token expired");
        }
        return claims;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}