package cz.upce.fei.redsys.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);

        try {
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (tokenProvider.isValid(token)) {
                    Authentication authentication = tokenProvider.toAuthentication(token);
                    log.debug("User '{}' authenticated successfully", authentication.getName());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.debug("Invalid or expired token for request: {}", request.getRequestURI());
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.debug("JWT processing failed: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    private String extractBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
