package com.myshop.userservice.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @NotBlank(message = "Поле 'username' обязательно")
    private String username;

    @NotBlank(message = "Поле 'email' обязательно")
    private String email;

    @NotBlank(message = "Поле 'password' обязательно")
    private String password;
}
