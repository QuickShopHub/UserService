package com.myshop.userservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailChange {
    private String email;
    private String password;

    @Email(message = "Некорректный формат email")
    private String newEmail;
}
