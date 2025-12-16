package cz.upce.fei.redsys.config;

import cz.upce.fei.redsys.security.JWTFilter;
import cz.upce.fei.redsys.security.RestAuthenticationEntryPoint;
import cz.upce.fei.redsys.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final Environment env;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> {
                    var permitted = List.of(
                            "/api/auth/register",
                            "/api/auth/login",
                            "/api/auth/request-password-reset",
                            "/api/auth/reset-password",
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/api/images",
                            "/api/images/**"
                    );

                    // Add H2 console only if the dev profile is active
                    String[] permittedArray = isDev
                            ? Stream.concat(permitted.stream(), Stream.of("/h2-console/**"))
                            .toArray(String[]::new)
                            : permitted.toArray(String[]::new);

                    auth.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll();
                    auth.requestMatchers(permittedArray)
                            .permitAll()
                            .anyRequest().authenticated();

                })
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint())
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> {
                    if (isDev) {
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable); // for H2 console
                    }
                    headers.cacheControl(cache -> {});
                });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(tokenProvider);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public SmartInitializingSingleton jwtSecretValidator(
            @Value("${security.jwt.secret:}") String secret) {
        return () -> {
            if (secret == null || secret.isBlank()) {
                throw new IllegalStateException("Missing required config: security.jwt.secret. Ensure .env with SECURITY_JWT_SECRET is in the working directory.");
            }
            if (secret.length() < 32) {
                throw new IllegalStateException("Invalid config: security.jwt.secret must be at least 32 characters (256 bits) long");
            }
        };
    }
}