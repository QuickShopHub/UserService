package com.myshop.userservice.controller;

import com.myshop.userservice.DTO.AuthDTO;
import com.myshop.userservice.DTO.UserDTO;
import com.myshop.userservice.DTO.ResponseDTO;
import com.myshop.userservice.service.AuthService;
import com.myshop.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/auth")
public class TokenController {
    private final UserService userService;
    private final AuthService authService;

    public TokenController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ResponseDTO> createToken(@RequestBody AuthDTO authDTO, HttpServletResponse response) {

        return authService.login(authDTO,  response);
    }

    @Transactional
    @PostMapping(path = "/signup")
    public ResponseEntity<ResponseDTO> addUser(@RequestBody UserDTO newUser, HttpServletResponse response) {

        if(userService.addUser(newUser) == 1){
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMessage("Email занят");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        AuthDTO authDTO =  new AuthDTO();
        authDTO.setEmail(newUser.getEmail());
        authDTO.setPassword(newUser.getPassword());
        return authService.login(authDTO, response);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshAccessToken(request, response);
    }


    @PostMapping(path = "/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }
}
