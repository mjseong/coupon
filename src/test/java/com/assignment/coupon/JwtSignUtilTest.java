package com.assignment.coupon;

import com.assignment.coupon.utils.CouponCodeUtils;
import com.assignment.coupon.utils.SignKeyUtils;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;


public class JwtSignUtilTest {

    @Test
    public void getSignKey(){
        String signKey = SignKeyUtils.saveHmcAndShaKey("HS256");
        System.out.println("key.size:"+signKey.length() +", key: " + signKey);        //hs512
        String time = Instant.now().truncatedTo(ChronoUnit.DAYS).toString().substring(0,10).replace("-","");
        long timeLong = Instant.now().toEpochMilli();

        timeLong = timeLong/1000;
        String timeString = String.valueOf(timeLong);

        String restore =Instant.ofEpochSecond(Long.parseLong(timeString)).toString();
        System.out.println("expireCode :"+timeLong+"-"+UUID.randomUUID());
        System.out.println("restore time:"+restore);
        System.out.println(Base64.getEncoder().encodeToString(time.getBytes()));

        CouponCodeUtils.generateCode();




    }
}
