package com.assignment.coupon.config.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.crypto.SecretKey;
import java.security.Key;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthProvider authProvider;
    private final AuthJwtProvider jwtProvider;

    @Value("${application.security.jwt.symmetric-signkey}")String encryptedKey;

    public SecurityConfig(AuthProvider authProvider, AuthJwtProvider jwtProvider){
        this.authProvider = authProvider;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginProcessingUrl("/signin")
                .successHandler(authSuccessHandler())
                .failureHandler(authFailureHandler())
                .and()
                .authorizeRequests(authRequst->
                        authRequst.antMatchers("/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthProvider());
    }

    @Bean
    public AuthenticationSuccessHandler authSuccessHandler(){
        return new AuthSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authFailureHandler(){
        return new AuthFailHandler();
    }

    @Bean
    public Key key(){
        //hs512(hmac-sha512) key
        Key key = Keys.hmacShaKeyFor(encryptedKey.getBytes());
        return key;
    }

    @Bean
    JwtDecoder jwtDecoder(){
        //Secret Key
        SecretKey key = (SecretKey) key();
        return NimbusJwtDecoder.withSecretKey(key).build();

    }

}
