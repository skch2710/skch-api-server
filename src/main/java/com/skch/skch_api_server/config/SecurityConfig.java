package com.skch.skch_api_server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;

	@Autowired
	private CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;

	private static final String JWT_ROLE_NAME = "authorities";
	private static final String ROLE_PREFIX = "";

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	String issuerUri;

//	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
//	String jwkUri;

	@Autowired
	private CookieBearerTokenResolver cookieBearerTokenResolver;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/api/v1/**").authorizeHttpRequests(auth -> auth
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll().anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.bearerTokenResolver(cookieBearerTokenResolver)
						.jwt(jwt -> jwt.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter()))
						.authenticationEntryPoint(customBearerTokenAuthenticationEntryPoint)
						.accessDeniedHandler(customBearerTokenAccessDeniedHandler))
				.csrf(Customizer.withDefaults())
				.headers(headers -> headers.xssProtection(Customizer.withDefaults())
						.contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'; object-src 'none'")))
				.build();
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
	}

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(JWT_ROLE_NAME);
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix(ROLE_PREFIX);

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;

	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}