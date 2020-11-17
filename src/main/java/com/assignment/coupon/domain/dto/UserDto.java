package com.assignment.coupon.domain.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserDto {

    @NotBlank
    @Size(max = 32)
    @Pattern(regexp = "^[\\w]*$")
    private String userId;

    @NotBlank
    @Size(min=6, max = 8)
    private String password;
}
