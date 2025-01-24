package com.ocP6.PayMyBuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le username ne peut être vide")
    String username;

    @Email(message = "L'email n'est pas correctement formaté")
    @NotNull(message = "L'email ne peut être null")
    String email;

    @NotBlank(message = "Le password ne peut être vide")
    String password;

}
