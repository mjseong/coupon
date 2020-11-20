package com.assignment.coupon.service.schedule;

import com.assignment.coupon.service.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CouponsExpirationScheduler {

    private final AsyncTaskService asyncTaskService;

    public CouponsExpirationScheduler(AsyncTaskService asyncTaskService){
        this.asyncTaskService = asyncTaskService;
    }

//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0/30 * * * * *")
    public void couponsExpiration(){
        log.info("Scheduler execute ");
        asyncTaskService.expireCoupons();
        asyncTaskService.notice3DaysExpirationCoupons();
    }







}
