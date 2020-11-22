package com.assignment.coupon.service.impl;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.entity.CouponHist;
import com.assignment.coupon.domain.state.EnumCouponState;
import com.assignment.coupon.repository.CouponHistRepository;
import com.assignment.coupon.service.CouponHistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponsHistoryImpl implements CouponHistService {

    private final CouponHistRepository couponHistRepository;

    public CouponsHistoryImpl(CouponHistRepository couponHistRepository) {
        this.couponHistRepository = couponHistRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public long bulkInsertLog(List<Coupon> coupons, EnumCouponState couponState){

//         List<CouponHist> couponHists = coupons.stream()
//                        .parallel()
//                        .map(coupon -> {
//                            return couponHistRepository.insertCouponLog(coupon.getCouponCode(),
//                                                                        coupon.getState(),
//                                                                        couponState.getEvent(),
//                                                                        coupon.getUserId(),
//                                                                        coupon.getIssuer(),
//                                                                        coupon.getCreateDate(),
//                                                                        coupon.getExpireDate(),
//                                                                        coupon.getUpdateDate());
//                        }).collect(Collectors.toList());

        //TODO: ID seq table 방식이 auto increment보다 빠름 그리고 JPA data에서 saveAll()이 루프 insert보다 빠름 이건 조사해봐야 듯알
        List<CouponHist> couponHists = coupons.stream()
                                        .parallel()
                                        .map(p->new CouponHist(p, couponState.getEvent()))
                                        .collect(Collectors.toList());
        //saveALL이 테스트 결과 더 빠름.
        couponHistRepository.saveAll(couponHists);

        return couponHists.size();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public long insertLog(Coupon coupon, EnumCouponState couponState){

        CouponHist couponHist = couponHistRepository.save(new CouponHist(coupon,couponState.getEvent()));

//        CouponHist couponHist = couponHistRepository.insertCouponLog(coupon.getCouponCode(),
//                                                                    coupon.getState(),
//                                                                    couponState.getEvent(),
//                                                                    coupon.getUserId(),
//                                                                    coupon.getIssuer(),
//                                                                    coupon.getCreateDate(),
//                                                                    coupon.getExpireDate(),
//                                                                    coupon.getUpdateDate());

        if(couponHist!=null){
            log.info("insert success");
            return 1;
        }
        log.info("insert faii");
        return 0;
    }
}
