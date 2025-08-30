package com.myshop.userservice.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailChange {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Email(message = "Некорректный формат email")
    private String newEmail;
}
