package com.assignment.coupon.controller;

import com.assignment.coupon.domain.dto.UserDto;
import com.assignment.coupon.domain.entity.User;
import com.assignment.coupon.service.impl.CustomUserDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
public class UserController {

    private final CustomUserDetailService customUserDetailService;

    public UserController(CustomUserDetailService customUserDetailService){
        this.customUserDetailService = customUserDetailService;
    }

    @PostMapping(value = "/signup")
    @ResponseBody
    public ResponseEntity signUp(@RequestBody @Valid UserDto userDto){

        User user = customUserDetailService.createUser(userDto.getUserName(),
                                                    userDto.getPassword(),
                                                    userDto.getAdminRole());

        return new ResponseEntity(user.getUsername(), HttpStatus.OK);

    }
}
