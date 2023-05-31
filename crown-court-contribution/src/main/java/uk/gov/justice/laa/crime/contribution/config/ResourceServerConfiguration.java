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

    public static final String API_PATH = "/api/**";
    public static final String SCOPE_READ = "SCOPE_READ";
    public static final String SCOPE_READ_WRITE = "SCOPE_READ_WRITE";

//    @Bean
//    protected BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint() {
//        BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint = new BearerTokenAuthenticationEntryPoint();
//        bearerTokenAuthenticationEntryPoint.setRealmName("Crown Court Proceedings API");
//        return bearerTokenAuthenticationEntryPoint;
//    }

//    @Bean
//    public AccessDeniedHandler bearerTokenAccessDeniedHandler() {
//        return new BearerTokenAccessDeniedHandler();
//    }

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

//                .oauth2ResourceServer(oauth2ResourceServer ->
//                        oauth2ResourceServer
//                                .accessDeniedHandler(bearerTokenAccessDeniedHandler())
//                                .authenticationEntryPoint(bearerTokenAuthenticationEntryPoint())
//                                .jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder))
//                );


        return http.build();
    }
}
