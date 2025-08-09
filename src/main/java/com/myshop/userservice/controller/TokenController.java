package com.myshop.userservice.controller;

import com.myshop.userservice.DTO.AuthDTO;
import com.myshop.userservice.DTO.UserDTO;
import com.myshop.userservice.service.AuthService;
import com.myshop.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class TokenController {
    private final UserService userService;
    private final AuthService authService;

    public TokenController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping(path = "/auth/login")
    public ResponseEntity<String> createToken(@RequestBody AuthDTO authDTO, HttpServletResponse response) {

        return authService.login(authDTO, response);
    }

    @Transactional
    @PostMapping(path = "/auth/signup")
    public ResponseEntity<String> addUser(@RequestBody UserDTO newUser, HttpServletResponse response) {

        if(userService.addUser(newUser) == 1){
            return ResponseEntity.badRequest().body("Email уже существует");
        }
        AuthDTO authDTO =  new AuthDTO();
        authDTO.setEmail(newUser.getEmail());
        authDTO.setPassword(newUser.getPassword());
        return authService.login(authDTO, response);
    }

    @PostMapping(path = "/auth/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshAccessToken(request, response);
    }

}
