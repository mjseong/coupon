package com.assignment.coupon.config.security;

import com.assignment.coupon.exception.UserCredentialNotValidException;
import com.assignment.coupon.service.impl.CustomUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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

        try{
            //1.UserDetailService find userId
            UserDetails user = userDetailsService.loadUserByUsername(userId);

            //2. password vaild check
            if(!passwordEncoder.matches(password,user.getPassword())){
                log.info("userName & password not valid");
                throw new UserCredentialNotValidException("userName & password not valid");
            }

            //3. userDetail
            details.put("user",user);

        }catch (NoSuchElementException e){
            log.info(e.getMessage());
            throw new UserCredentialNotValidException("userName & password not valid");
        }

        //4. role
        List<String> roles = Arrays.asList("ROLE_USER");
        Collection<GrantedAuthority> grants = roles.stream()
                .map(p->new SimpleGrantedAuthority(p))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId, password, grants);
        token.setDetails(details);

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
