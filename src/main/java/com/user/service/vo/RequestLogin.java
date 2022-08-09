package com.user.service.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {
    @NotNull(message = "email cannot be null")
    @Size(min = 2, message = "email not be less than two char")
    @Email
    private String email;

    @NotNull
    @Size(min= 2, message =  "password must not be empty")
    private String password;


}
