package com.assignment.coupon.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    @NotBlank
    @Size(max = 32)
    @Pattern(regexp = "^[\\w||-]*$")
    private String userName;

    @NotBlank
    @Size(min=6, max = 8)
    private String password;

    private Boolean adminRole = false;
}
