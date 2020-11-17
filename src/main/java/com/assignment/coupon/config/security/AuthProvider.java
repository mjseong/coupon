package com.assignment.coupon.config.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthProvider implements AuthenticationProvider {


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userId = authentication.getName();
        String password = (String) authentication.getCredentials();

        //1.UserDetailService find userId

        //2. password vaild check

        //3. role
        List<String> roles = Arrays.asList("ROLE_USER");
        Collection<GrantedAuthority> grants = roles.stream()
                .map(p->new SimpleGrantedAuthority(p))
                .collect(Collectors.toList());

        //4. userDetail
        Map<String, Object> details = new HashMap<>();
        details.put("","");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId, password, grants);
        token.setDetails(details);

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }

}
