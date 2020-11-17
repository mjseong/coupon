package com.assignment.coupon.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;

@Slf4j
public class SignKeyUtils {

    public static String saveHs512Key(){
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String symmetricKey = Encoders.BASE64.encode(key.getEncoded());
        log.debug("hs512: "+ symmetricKey);
        return symmetricKey;
    }
}
