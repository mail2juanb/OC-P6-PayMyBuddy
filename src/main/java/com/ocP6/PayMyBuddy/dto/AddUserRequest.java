package com.ocP6.PayMyBuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class AddUserRequest {

    @NotBlank(message = "username shouldn't be null or blank")
    String username;

    @Email(message = "email must be valid")
    String email;

    @NotBlank(message = "password shouldn't be null or blank")
    String password;
}
