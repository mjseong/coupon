package com.assignment.coupon.controller;

import java.util.List;

//TODO: 공통적으로 뭔가 사용할떄 쓰려했는대 시간날때 다시 구현해야지
@Deprecated
public abstract class BaseController<T> {

    protected boolean hasRoleAdmin(String tokenUserId, String reqUserId, List<String> authorities){

        //admin User is handling any-api
        if(!tokenUserId.equals(reqUserId) && authorities.contains("ROLE_ADMIN"))
            return true;

        return false;
    }
}
