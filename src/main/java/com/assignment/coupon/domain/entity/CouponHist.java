package com.assignment.coupon.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "coupon_hist",
        uniqueConstraints = {@UniqueConstraint(name = "idx_uniq_coupon_info_01", columnNames = {"coupon_code","coupon_state","coupon_event","coupon_udate"})},
        indexes = {@Index(name="idx_coupon_hist_01", columnList = "chist_cdate")})
@SequenceGenerator(name = "COUPONHIST_SEQ_GEN",
        sequenceName = "COUPON_HIST_SEQ",
        allocationSize = 100
)
@Data
public class CouponHist {

    public CouponHist(Coupon coupon, String event){
        this.couponCode = coupon.getCouponCode();
        this.userId = coupon.getUserId();
        this.issuer = coupon.getIssuer();
        this.state = coupon.getState();
        this.event = event;
        this.createDate = coupon.getCreateDate();
        this.expireDate = coupon.getExpireDate();
        this.updateDate = coupon.getUpdateDate();
        this.logDate = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COUPONHIST_SEQ_GEN")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "chist_id")
    private long id;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "coupon_state")
    private String state;

    @Column(name = "coupon_event")
    private String event;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "coupon_issuer")
    private String issuer;

    @Column(name = "coupon_cdate")
    private Instant createDate;

    @Column(name = "coupon_edate")
    private Instant expireDate;

    @Column(name = "coupon_udate")
    private Instant updateDate;

    @CreatedDate
    @Column(name = "chist_cdate", insertable = true, columnDefinition="datetime DEFAULT CURRENT_TIMESTAMP")
    private Instant logDate;
}
