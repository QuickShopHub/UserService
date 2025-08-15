package com.myshop.userservice.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @NotBlank(message = "Поле 'username' обязательно")
    private String username;


    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Поле 'password' обязательно")
    private String password;

    private boolean admin = false;
}
