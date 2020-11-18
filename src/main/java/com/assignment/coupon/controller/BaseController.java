package com.assignment.coupon.controller;

import java.util.List;

public abstract class BaseController {

    protected boolean hasRoleAdmin(String tokenUserId, String reqUserId, List<String> authorities){

        //admin User is handling any-api
        if(!tokenUserId.equals(reqUserId) && authorities.contains("ROLE_ADMIN"))
            return true;

        return false;
    }
}
