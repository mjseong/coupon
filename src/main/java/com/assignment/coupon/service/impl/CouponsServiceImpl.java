package com.assignment.coupon.service.impl;

import com.assignment.coupon.domain.dto.CouponCountDto;
import com.assignment.coupon.domain.dto.CouponDto;
import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import com.assignment.coupon.exception.CouponServiceException;
import com.assignment.coupon.repository.CouponRepository;
import com.assignment.coupon.service.CouponService;
import com.assignment.coupon.service.CouponHistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CouponsServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponHistService couponHistService;

    public CouponsServiceImpl(CouponRepository couponRepository,
                              CouponHistService couponHistService){
        this.couponRepository = couponRepository;
        this.couponHistService = couponHistService;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Coupon> createCoupon(long count, Instant expDate){

//        StopWatch stopWatch = new StopWatch();
        List<Coupon> coupons = Stream.generate(()->Coupon.newCoupon("ubot_corp", expDate))
                                                        .parallel()
                                                        .limit(count)
                                                        .collect(Collectors.toList());

        long saveCount = couponRepository.saveAll(coupons).size();

//        stopWatch.start();
        long saveHistCount = couponHistService.bulkInsertLog(coupons,EnumCouponState.ISSUE);
//        stopWatch.stop();
//        log.info("couponHistService.bulkInsertLog : " + stopWatch.getTotalTimeSeconds());

        if(saveHistCount==saveCount){
            log.info("Issue history Insert complete");
        }

        if(saveCount!=count){
            throw new CouponServiceException("Issue fail count : " + count);
        }
        return coupons;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CouponDto assignCoupon(String couponCode, String userId) {

        Coupon coupon = couponRepository.findCouponByCouponCodeAndUserIdIsNullAndState(couponCode, EnumCouponState.ISSUE.getState())
                                        .orElseThrow(NoSuchElementException::new);

        coupon.setUserId(userId);
        coupon.setState(EnumCouponState.ASSIGN.getState());

        //log assign record
        couponHistService.insertLog(coupon, EnumCouponState.ASSIGN);

        return new CouponDto(coupon.getCouponCode(),
                                coupon.getIssuer(),
                                coupon.getState(),
                                coupon.getUserId(),
                                coupon.getCreateDate(),
                                coupon.getExpireDate());
    }

    @Override
    public List<String> bulkAssignCoupon(List<String> couponCodes, String userId) {

        //check used couponCodes
//        couponRepository.findCouponByIdAndUserIdIsNullAndState();

        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public List<CouponDto> findCouponsByUserId(String userId) {
        return couponRepository.findCouponsByUserIdAndState(userId, EnumCouponState.ASSIGN.getState()).stream()
                .map(p->new CouponDto(p.getCouponCode(),
                                        p.getIssuer(),
                                        p.getState(),
                                        p.getUserId(),
                                        p.getCreateDate(),
                                        p.getExpireDate()))
                                    .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public List<CouponDto> findCouponsByCreateTodayAndExipiredState(Instant toDay, Pageable pageable) {
        Instant toCreateDate = toDay.plus(Duration.ofDays(1));
        return couponRepository.findCouponsByCreateDateBetweenAndState(toDay,toCreateDate,EnumCouponState.EXPIRE.getState(), pageable).stream()
                .map(p->new CouponDto(p.getCouponCode(),
                                        p.getIssuer(),
                                        p.getState(),
                                        p.getUserId(),
                                        p.getCreateDate(),
                                        p.getExpireDate()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public List<Coupon> findCouponsByExpireDateBetweenAndState(Instant fromExpireDate, Instant toExpireDate, String state) {

        if(state!=null && !state.equals("")){
            return couponRepository.findCouponsByExpireDateBetweenAndState(fromExpireDate,toExpireDate,state);
        }

        return couponRepository.findCouponsByExpireDateBetween(fromExpireDate, toExpireDate);

    }

    @Override
    public String expireNotice(String couponCode, String userId) {
        log.info(String.format("Dear %s, \n you have 3days left for coupon(%s) exipration Date ",userId, couponCode));
        return couponCode;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public CouponDto findByCouponCodeAndStateAndUserNotNull(String couponCode, String state) {

        CouponDto coupon = couponRepository.findByCouponCode(couponCode)
                                    .filter(f->f.getState().equals(state) && f.getUserId()!=null)
                                    .map(p->new CouponDto(p.getCouponCode(),p.getIssuer(), p.getState(), p.getUserId(), p.getCreateDate(),p.getExpireDate()))
                                    .orElseThrow(()->new NoSuchElementException("not found couponCode: "+couponCode));
        return coupon;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CouponDto useCoupon(String couponCode, String userId) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                                    .filter(f->f.getState().equals(EnumCouponState.ASSIGN.getState()) && f.getUserId()!=null&& f.getUserId().equals(userId))
                                    .orElseThrow(()->new NoSuchElementException("your couponCode invalid or not assign: "+couponCode));

        coupon.setState(EnumCouponState.USE.getState());

        //log use event record
        couponHistService.insertLog(coupon, EnumCouponState.USE);

        return new CouponDto(coupon.getCouponCode(),
                coupon.getIssuer(),
                coupon.getState(),
                coupon.getUserId(),
                coupon.getCreateDate(),
                coupon.getExpireDate());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CouponDto cancelCoupon(String couponCode, String userId) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                                    .filter(f->f.getState().equals(EnumCouponState.USE.getState()) && f.getUserId()!=null && f.getUserId().equals(userId))
                                    .orElseThrow(()->new NoSuchElementException("your couponCode not found or not used state:"+couponCode));

        coupon.setState(EnumCouponState.CANCEL.getState());

        //log cancel event record
        couponHistService.insertLog(coupon, EnumCouponState.CANCEL);

        return new CouponDto(coupon.getCouponCode(),
                                coupon.getIssuer(),
                                coupon.getState(),
                                coupon.getUserId(),
                                coupon.getCreateDate(),
                                coupon.getExpireDate());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Coupon updateCouponState(Coupon coupon, String state) {
        coupon.setState(state);
        return couponRepository.save(coupon);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Coupon> updateALLCoupon(List<Coupon> coupons) {
        return couponRepository.saveAll(coupons);
    }
}
