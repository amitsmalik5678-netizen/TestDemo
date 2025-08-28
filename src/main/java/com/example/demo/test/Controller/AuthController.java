package com.example.demo.test.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

	private final JwtEncoder jwtEncoder;

	public AuthController(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	@Operation(summary = "Issue JWT token (demo, no credentials)")
	@PostMapping("/token")
	public ResponseEntity<Map<String, String>> token(@RequestBody(required = false) Map<String, Object> body) {
		String subject = body != null && body.get("sub") != null ? String.valueOf(body.get("sub")) : "demo-user";
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuedAt(now)
			.expiresAt(now.plus(1, ChronoUnit.HOURS))
			.subject(subject)
			.claim("scope", "messages:read")
			.issuer("demo-app")
			.build();
		String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		return ResponseEntity.ok(Map.of("access_token", token, "token_type", "Bearer"));
	}
}



