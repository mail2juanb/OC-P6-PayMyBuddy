package com.ocP6.PayMyBuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionRequest {


    @Email(message = "L'email n'est pas correctement formaté" )
    @NotNull(message = "L'email ne peut être null")
    private String email;


}
