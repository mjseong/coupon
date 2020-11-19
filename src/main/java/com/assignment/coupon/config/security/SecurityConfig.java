package com.assignment.coupon.config.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.crypto.SecretKey;
import java.security.Key;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthProvider authProvider;
    private final AuthJwtHandler jwtHandler;

    @Value("${application.security.jwt.symmetric-signkey}")String encryptedKey;

    public SecurityConfig(AuthProvider authProvider, AuthJwtHandler jwtHandler){
        this.authProvider = authProvider;
        this.jwtHandler = jwtHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                                authorizeRequests
                                    .antMatchers("/signup","/auth-test").permitAll()
                                    .antMatchers(HttpMethod.GET, "/authtoken-test").hasAnyAuthority("SCOPE_coupon:read")
                                    .antMatchers(HttpMethod.POST, "/api/coupons").hasAnyAuthority("SCOPE_coupon_admin:write")
                                    .antMatchers(HttpMethod.PUT, "/api/coupons/**/users/**/**").hasAnyAuthority("SCOPE_coupon:write")
                                    .anyRequest().authenticated()
                )
                .formLogin()
                    .permitAll()
                    .usernameParameter("userName")
                    .loginProcessingUrl("/signin")
                    .successHandler(authSuccessHandler())
                    .failureHandler(authFailureHandler())
                .and()
                .csrf().disable()
                .cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .httpBasic().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Bean
    public AuthenticationSuccessHandler authSuccessHandler(){
        return new AuthSuccessHandler(jwtHandler, key());
    }

    @Bean
    public AuthenticationFailureHandler authFailureHandler(){
        return new AuthFailHandler();
    }

    @Bean
    public Key key(){
        //HS256(hmac-sha256) key
        Key key = Keys.hmacShaKeyFor(encryptedKey.getBytes());
        return key;
    }

    @Bean
    JwtDecoder jwtDecoder(){
        //Secret Key
        //jose HS512 not support & support HMAC HS256
        SecretKey key = (SecretKey) key();
        return NimbusJwtDecoder.withSecretKey(key).build();

    }

}
