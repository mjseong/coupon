package com.assignment.coupon.domain.state;

public enum EnumCouponState {

    ISSUE("ISSUE","ISSUED"),
    ASSIGN("ASSIGN","ASSIGNED"),
    CANCEL("CANCEL","ASSIGNED"),
    USE("USE","USED"),
    EXPIRE("EXPIRE","EXPIRED");

    private String event;
    private String state;

    EnumCouponState(String event, String state){
        this.event = event;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public String getEvent() {
        return event;
    }
}
