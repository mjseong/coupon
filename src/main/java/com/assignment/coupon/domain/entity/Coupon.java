package com.assignment.coupon.domain.entity;

import com.assignment.coupon.domain.state.EnumCouponState;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coupon_info",
        uniqueConstraints = {@UniqueConstraint(name = "idx_uniq_coupon_info_01", columnNames = {"coupon_code"})},
        indexes = {@Index(name="idx_coupon_info_01", columnList = "coupon_edate,coupon_state"),
                @Index(name="idx_coupon_info_02", columnList = "coupon_cdate,coupon_state")})
@SequenceGenerator(name = "COUPON_SEQ_GEN",
                    sequenceName = "COUPON_SEQ",
                    allocationSize = 100
)
public class Coupon{

    public Coupon() {}

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COUPON_SEQ_GEN")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private long id;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "coupon_issuer")
    private String issuer;

    @Column(name = "coupon_state")
    private String state;

    @CreatedDate
    @Column(name = "coupon_cdate")
    private Instant createDate;

    @Column(name = "coupon_edate")
    private Instant expireDate;

    @Column(name = "coupon_udate", columnDefinition="datetime DEFAULT CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private Instant updateDate;

    public Coupon(Builder builder) {
        this.couponCode = builder.couponCode;
        this.userId = builder.userId;
        this.issuer = builder.issuer;
        this.state = builder.state;
        this.createDate = builder.createDate;
        this.updateDate = builder.updateDate;
        this.expireDate = builder.expireDate;
    }

    public Builder Coupon() {
        return new Builder();
    }

    public static class Builder {
        private String couponCode = UUID.randomUUID().toString();
        private Instant createDate = Instant.now();
        private Instant updateDate = Instant.now();
        private Instant expireDate = Instant.now().plus(Duration.ofHours(24));

        private String userId;
        private String issuer;
        private String state;

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }
        public Builder issuer(String issuer){
            this.issuer = issuer;
            return this;
        }

        public Builder state(EnumCouponState state){
            this.state = state.getState();
            return this;
        }

        public Builder expireDate(Instant expireDate){
            if(expireDate!=null){
                this.expireDate = expireDate;
            }
            return this;
        }

        public Coupon build(){
            return new Coupon(this);
        }

    }

    public static Builder Builder(){
        return new Builder();
    }

    public static Coupon newCoupon(String issuer, Instant expireDate){
        return Builder().issuer(issuer)
                        .state(EnumCouponState.ISSUE)
                        .expireDate(expireDate)
                        .build();
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public Instant getExpireDate() {
        return expireDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public long getId() {
        return id;
    }
}
