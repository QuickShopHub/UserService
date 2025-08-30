package com.myshop.userservice.controller;

import com.myshop.userservice.DTO.*;
import com.myshop.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(path = "/api/user")
@EnableWebSecurity
@CrossOrigin(origins = {"http://26.94.50.207:4200", "http://localhost:4200", "http://localhost:80", "http://localhost", "http://185.161.64.35"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = "/email")
    public ResponseEntity<Map<String, String>> updateEmail(@RequestBody EmailChange emailChange) {
        return userService.updateEmail(emailChange);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = "/username")
    public ResponseEntity<Map<String, String>> updateName(@RequestBody NameChange nameChange) {
        return userService.updateName(nameChange);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = "/password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody PasswordChange passwordChange) {
        return userService.updatePassword(passwordChange);
    }
}
