package com.assignment.coupon;

import com.assignment.coupon.utils.SignKeyUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JwtSignUtilTest {

    @Test
    public void getSignKey(){
        String signKey = SignKeyUtils.saveHmcAndShaKey("HS256");
        System.out.println("key.size:"+signKey.length() +", key: " + signKey);        //hs512
    }
}
