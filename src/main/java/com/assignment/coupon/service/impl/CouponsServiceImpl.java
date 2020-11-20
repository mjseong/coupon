package com.assignment.coupon.service.impl;

import com.assignment.coupon.domain.dto.CouponCountDto;
import com.assignment.coupon.domain.dto.CouponDto;
import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import com.assignment.coupon.exception.CouponServiceException;
import com.assignment.coupon.repository.CouponRepository;
import com.assignment.coupon.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CouponsServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponsServiceImpl(CouponRepository couponRepository){
        this.couponRepository = couponRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CouponCountDto createCoupon(long count, Instant expDate){

        List<Coupon> coupons = Stream.generate(()->Coupon.newCoupon("ubot_corp", expDate))
                                                        .limit(count)
                                                        .collect(Collectors.toList());

        long saveCount = couponRepository.saveAll(coupons).size();

        if(saveCount!=count){
            throw new CouponServiceException("Issue fail count : " + count);
        }
        return new CouponCountDto(coupons.size());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CouponDto assignCoupon(String couponCode, String userId) {

        Coupon coupon = couponRepository.findCouponByCouponCodeAndUserIdIsNullAndState(couponCode, EnumCouponState.ISSUE.getState())
                                        .orElseThrow(NoSuchElementException::new);

        coupon.setUserId(userId);
        coupon.setState(EnumCouponState.ASSIGN.getState());

        return new CouponDto(coupon.getCouponCode(), coupon.getIssuer(), coupon.getCreateDate(), coupon.getExpireDate());
    }

    @Override
    public List<String> bulkAssignCoupon(List<String> couponCodes, String userId) {

        //check used couponCodes
//        couponRepository.findCouponByIdAndUserIdIsNullAndState();

        return null;
    }

    @Override
    public List<CouponDto> findCouponsByUserId(String userId) {
        return couponRepository.findCouponsByUserIdAndState(userId, EnumCouponState.ASSIGN.getState())
                                    .stream()
                                    .map(p->new CouponDto(p.getCouponCode(), p.getIssuer(), p.getCreateDate(), p.getExpireDate()))
                                    .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public Coupon findByCouponCodeAndStateAndUserNotNull(String couponCode, String state) {

        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                                    .filter(f->f.getState().equals(state) && f.getUserId()!=null)
                                    .orElseThrow(()->new NoSuchElementException("not found couponCode: "+couponCode));
        return coupon;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CouponDto useCoupon(String couponCode, String userId) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                                    .filter(f->f.getState().equals(EnumCouponState.ASSIGN.getState()) && f.getUserId()!=null&& f.getUserId().equals(userId))
                                    .orElseThrow(()->new NoSuchElementException("your couponCode not found or not assign: "+couponCode));

        coupon.setState(EnumCouponState.USE.getState());

        return new CouponDto(coupon.getCouponCode(), coupon.getIssuer(), coupon.getCreateDate(), coupon.getExpireDate());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CouponDto cancelCoupon(String couponCode, String userId) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                                    .filter(f->f.getState().equals(EnumCouponState.USE.getState()) && f.getUserId()!=null && f.getUserId().equals(userId))
                                    .orElseThrow(()->new NoSuchElementException("your couponCode not found or not assign:"+couponCode));

        coupon.setState(EnumCouponState.CANCEL.getState());

        return new CouponDto(coupon.getCouponCode(), coupon.getIssuer(), coupon.getCreateDate(), coupon.getExpireDate());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Coupon updateCouponState(Coupon coupon, String state) {
        coupon.setState(state);
        return couponRepository.save(coupon);
    }
}
