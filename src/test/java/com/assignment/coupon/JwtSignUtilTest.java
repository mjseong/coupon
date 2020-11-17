package com.assignment.coupon;

import com.assignment.coupon.utils.SignKeyUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JwtSignUtilTest {

    @Test
    public void getSignKey(){
        String signKey = SignKeyUtils.saveHs512Key();
        //hs512
        Assertions.assertEquals(128, signKey.length());
    }
}
