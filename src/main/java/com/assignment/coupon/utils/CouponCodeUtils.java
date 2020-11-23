package com.assignment.coupon.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
public class CouponCodeUtils {

    //TODO: sharding couponCode 생성은 나중에 좀더 연구해야 할듯 uuid를 대체할만한 유닉키를 찾아 붙이면 될듯 싶은대 ...
    public static String generateCodeInExpire(Instant expDate){

        long expireDateMillis = expDate.toEpochMilli();
        long createDateMillis = Instant.now().toEpochMilli();
        long createDateSec = Instant.now().getEpochSecond();

        log.debug("expireDateMillis: "+ expireDateMillis);
        log.debug("createDateMillis: "+createDateMillis);
        log.debug("sec: "+createDateSec);
        //DateDayHour
        long expireDateHour = (((expireDateMillis/1000)/60)/60);

        String strExpDate = String.valueOf(expireDateHour);
        String[] strExpArray = strExpDate.split("");
        String expToAsc = "";
        for(String chr:strExpArray){
            //ascii decimal match 80 ~ 89 alphabet
            int i = Integer.parseInt(chr) + 80;
            expToAsc += (char)i;
        }

        log.debug("expDateToAscChar:"+expToAsc);
        String couponCode = expToAsc.toLowerCase()+"-"+ UUID.randomUUID();

        return couponCode;
    }

    public static String generateCode(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Instant expDate = Instant.now().minus(Duration.ofDays(18000)).truncatedTo(ChronoUnit.DAYS);

        log.debug("expDate :"+expDate);
        long expireDateMillis = expDate.toEpochMilli();

        long createDateMillis = Instant.now().toEpochMilli();

        long expireDateSec = expireDateMillis/1000;
        long expireDateMin = expireDateSec/60;
        long expireDateHour = (((expireDateMillis/1000)/60)/60);

        String strExpDate = String.valueOf(expireDateHour);
        String[] strExpArray = strExpDate.split("");
        String expToAsc = "";
        for(String chr:strExpArray){
            //ascii decimal match 80 ~ 89 alphabet
            int i = Integer.parseInt(chr) + 80;
            expToAsc += (char)i;
        }

        log.debug("expDateToAscChar:"+expToAsc);
        String couponCode = expToAsc.toLowerCase()+"-"+ UUID.randomUUID();
        stopWatch.stop();
        log.debug("createDateMillis: "+createDateMillis);

        log.debug("expireDateMillis: "+ expireDateMillis);
        log.debug("expireDateSec: "+expireDateSec);
        log.debug("expireDateMin: "+expireDateMin);
        log.debug("expireDate: "+expireDateHour);

        log.debug("restore expireDateMillis : "+ (((expireDateHour*60)*60)*1000));

        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        LocalDate date = LocalDate.of(dateTime.getYear(), dateTime.getMonth(),dateTime.getDayOfMonth());
        int weekOfYear = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        log.debug("millis: "+createDateMillis);
        log.debug("weekOfYear:"+weekOfYear);

        log.debug("couponCode :"+couponCode);

        log.debug(stopWatch.shortSummary());
        log.debug(stopWatch.prettyPrint());

        return couponCode;
    }
}

