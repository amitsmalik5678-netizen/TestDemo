package com.example.demo.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.jwt.secret:changeit-demo-secret-change-me}")
    private String jwtSecret;

    @Value("${app.jwt.fixed:}")
    private String fixedToken;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/users/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .httpBasic(basic -> basic.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthEntryPoint()));

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        JwtDecoder delegate = NimbusJwtDecoder.withSecretKey(key).build();
        return token -> {
            if (fixedToken != null && !fixedToken.isEmpty() && fixedToken.equals(token)) {
                java.time.Instant now = java.time.Instant.now();
                java.util.Map<String, Object> headers = java.util.Map.of("alg", "HS256");
                java.util.Map<String, Object> claims = new java.util.HashMap<>();
                claims.put("sub", "fixed-token-user");
                claims.put("scope", "users:read");
                return new Jwt(token, now, now.plus(java.time.Duration.ofHours(12)), headers, claims);
            }
            return delegate.decode(token);
        };
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    @Bean
    public AuthenticationEntryPoint restAuthEntryPoint() {
        return new RestAuthEntryPoint();
    }
}


