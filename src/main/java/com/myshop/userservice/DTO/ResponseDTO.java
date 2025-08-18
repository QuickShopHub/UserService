package com.myshop.userservice.DTO;

import com.myshop.userservice.repository.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {
    String token;
    User user;
    String message;
}
