package com.assignment.coupon.domain.state;

public enum EnumCouponState {

    ISSUE("ISSUE"),
    ASSIGN("ASSIGN"),
    CANCEL("CANCEL"),
    USED("USED"),
    EXPIRE("EXPIRE");

    private String state;

    EnumCouponState(String state){
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
