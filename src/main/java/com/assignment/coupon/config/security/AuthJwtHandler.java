package com.assignment.coupon.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class AuthJwtHandler {

    public String createJwt(String subject, String issuner, Key key, int minute) {

        String jwt = "";
        String jti = UUID.randomUUID().toString();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        cal.add(Calendar.MINUTE, minute);

        List<String> scops = new ArrayList<>();

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("sub", subject);
        claims.put("iss", issuner);
        claims.put("scopes",scops);

        try {
            jwt = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(cal.getTimeInMillis()))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setId(jti)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Create JsonWebToken Error",e);
        }

        return jwt;
    }

    public String verifyToken(String jwToken, Key key) {

        if(jwToken != null) {

            String sub = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(jwToken.replace("Bearer", ""))
                    .getBody()
                    .getSubject();

            return sub;

        }
        return null;
    }

}
