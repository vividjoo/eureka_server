package com.user.service.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message = "Email cannot be null!")
    @Size(min=2, message = "email not be less than two char")
    @Email
    private String email;

    @Size(min=2, message = "name not be less than two char")
    @NotNull(message = "name must be")
    private String name;

    @Size(min=3, message = "password not be less than two char")
    @NotNull(message = "password must be!")
    private String pwd;


}
