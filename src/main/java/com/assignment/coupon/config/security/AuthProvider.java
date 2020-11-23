package com.assignment.coupon.config.security;

import com.assignment.coupon.exception.UserCredentialNotValidException;
import com.assignment.coupon.service.impl.CustomUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class AuthProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthProvider(CustomUserDetailService customUserDetailService,
                        PasswordEncoder passwordEncoder){
        this.userDetailsService = customUserDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userId = authentication.getName();
        String password = (String) authentication.getCredentials();
        Map<String, Object> details = new HashMap<>();
        Collection<GrantedAuthority> roles;

        try{
            //1.UserDetailService find userId
            UserDetails user = userDetailsService.loadUserByUsername(userId);
            roles = (Collection<GrantedAuthority>) user.getAuthorities();

            //2. password vaild check
            if(!passwordEncoder.matches(password,user.getPassword())){
                log.debug("userName & password not valid");
                throw new UserCredentialNotValidException("userName & password not valid");
            }

            //3. userDetail
            details.put("user",user);

        }catch (UsernameNotFoundException e){
            log.debug(e.getMessage());
            throw new UserCredentialNotValidException("userName & password not valid");
        }

        if(roles != null){
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId, password, roles);
            token.setDetails(details);
            return token;
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
