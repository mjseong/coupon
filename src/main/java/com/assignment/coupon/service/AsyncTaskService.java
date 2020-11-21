package com.assignment.coupon.service;

import java.util.concurrent.CompletableFuture;

public interface AsyncTaskService {
    public CompletableFuture<Boolean> expireCoupons();
    public CompletableFuture<Long> notice3DaysExpirationCoupons();

}
