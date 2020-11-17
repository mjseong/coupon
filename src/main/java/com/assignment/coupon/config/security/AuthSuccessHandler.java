package com.assignment.coupon.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthJwtHandler authJwtHandler;
    private final Key secretKey;

    private final String issuner = "Coupon_kakao";


    public AuthSuccessHandler(AuthJwtHandler authJwtHandler, Key key){
        this.authJwtHandler = authJwtHandler;
        this.secretKey = key;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Map<String,Object> map = new HashMap();

        if(authentication!=null){
            String userId = authentication.getName();
            List<String> scops = Arrays.asList("coupon:read","coupon:write");

            String accessToken = authJwtHandler.createJwt(userId, issuner, secretKey, 30, scops);
            map.put("access_token", accessToken);
        }
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(map);

        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(body);
    }
}
