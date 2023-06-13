package uk.gov.justice.laa.crime.contribution.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@Order(1)
@EnableWebSecurity
public class ResourceServerConfiguration {

    private static final String API_PATH = "/api/**";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**")
                        .permitAll()
                        .requestMatchers("/open-api/**")
                        .permitAll()
                        .requestMatchers("/actuator/**")
                        .permitAll()
                        .requestMatchers("/error")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, API_PATH)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, API_PATH)
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT, API_PATH)
                        .permitAll()
                        .requestMatchers(HttpMethod.DELETE, API_PATH)
                        .permitAll()
                        .requestMatchers(HttpMethod.PATCH, API_PATH)
                        .permitAll()
                        .anyRequest().anonymous()
                ).csrf().disable();

        return http.build();
    }
}
