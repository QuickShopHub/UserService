package com.myshop.userservice.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChange {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String newPassword;
}