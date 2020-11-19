package com.assignment.coupon.controller;


import com.assignment.coupon.domain.dto.UserDto;
import com.assignment.coupon.domain.entity.User;
import com.assignment.coupon.service.impl.UserDetailServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
public class UserController {

    private final UserDetailServiceImpl userDetailServiceImpl;

    public UserController(UserDetailServiceImpl userDetailServiceImpl){
        this.userDetailServiceImpl = userDetailServiceImpl;
    }

    @PostMapping(value = "/signup")
    @ResponseBody
    public ResponseEntity signUp(@RequestBody @Valid UserDto userDto){

        User user = userDetailServiceImpl.createUser(userDto.getUserName(),
                                                    userDto.getPassword(),
                                                    userDto.getAdminRole());

        return new ResponseEntity(user.getUsername(), HttpStatus.OK);

    }

    @GetMapping(value = "/auth-test")
    public ResponseEntity authTest(){
        return new ResponseEntity("security pass", HttpStatus.OK);
    }

    @GetMapping(value = "/authtoken-test")
    public ResponseEntity authTokenTest(@AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getSubject();
        return new ResponseEntity("security pass", HttpStatus.OK);
    }
}
