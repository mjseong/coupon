package com.assignment.coupon.domain.entity;

import com.assignment.coupon.domain.state.EnumCouponState;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coupon_info")
public class Coupon{

    public Coupon() {}

    @Id
    @Column(name = "coupon_id")
    private String couponCode;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "coupon_issuer")
    private String issuer;

    @Column(name = "coupon_state")
    @Enumerated(EnumType.STRING)
    private EnumCouponState couponState;

    @CreatedDate
    @Column(name = "coupon_cdate")
    private Instant createDate;

    @Column(name = "coupon_udate")
    @UpdateTimestamp
    private Instant updateDate;

    public Coupon(Builder builder) {
        this.couponCode = builder.couponCode;
        this.userId = builder.userId;
        this.issuer = builder.issuer;
        this.couponState = builder.couponState;
        this.createDate = builder.createDate;
        this.updateDate = builder.updateDate;
    }

    public Builder Coupon() {
        return new Builder();
    }

    public static class Builder {
        private String couponCode = UUID.randomUUID().toString();
        private Instant createDate = Instant.now();
        private Instant updateDate = null;

        private String userId;
        private String issuer;
        private EnumCouponState couponState;

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }
        public Builder issuer(String issuer){
            this.issuer = issuer;
            return this;
        }

        public Builder couponState(EnumCouponState couponState){
            this.couponState = couponState;
            return this;
        }

        public Builder createDate(Instant createDate){
            this.createDate = createDate;
            return this;
        }

        public Coupon build(){
            return new Coupon(this);
        }

    }

    public static Builder Builder(){
        return new Builder();
    }

    public static Coupon newCoupon(String issuer){
        return Builder().issuer(issuer)
                        .couponState(EnumCouponState.ISSUE)
                        .build();
    }

    public String getCouponCode() {
        return couponCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getIssuer() {
        return issuer;
    }

    public EnumCouponState getCouponState() {
        return couponState;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }
}
