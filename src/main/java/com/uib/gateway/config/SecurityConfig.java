package com.uib.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.Cookie;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

import javax.crypto.spec.SecretKeySpec;


@Configuration 
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig 
{
    @Value("${jwt.secret}")
    String jwtKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception
    {
        http.
            //oauth2Login(null).
            csrf(csrf -> csrf.disable()).
            cors(cors -> cors.configurationSource(corsConfigurationSource())).
            authorizeHttpRequests(
                requests -> requests.
                    requestMatchers( "/user/login", "/user/regsiter").permitAll().
                    requestMatchers("/user/**", "/document/**").hasRole("ADMIN").
                    anyRequest().authenticated()
            ).
            oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt. decoder(jwtDecoder()))).
            //httpBasic(withDefaults()).
            userDetailsService(userDetailsService);
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
            new SecretKeySpec(jwtKey.getBytes(), "HS256")
        ).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Allow Angular dev server
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
    
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
    }

    /* private Cookie createHttpOnlyCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS only
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 1 hour
        return cookie;
    } */

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(true);
    }

}
