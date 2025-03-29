package com.uib.gateway.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration 
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig 
{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception
    {
        http.
            csrf(csrf -> csrf.disable()).
            authorizeHttpRequests(
                requests ->
                    {
                        requests.
                            requestMatchers("/admin/*", "/customer/*").hasRole("ADMIN").
                            //requestMatchers("/customer/*").hasRole("CUSTOMER").
                            anyRequest().permitAll();
                            //anyRequest().authenticated();
                    }
            ).httpBasic(withDefaults()).
            userDetailsService(userDetailsService);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}
