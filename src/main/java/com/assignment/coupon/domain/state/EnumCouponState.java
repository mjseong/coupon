package com.assignment.coupon.domain.state;

public enum EnumCouponState {

    //싱태: 발급, 지급, 사용, 만료
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
