package com.myshop.userservice.controller;

import com.myshop.userservice.DTO.*;
import com.myshop.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/api/user")
@EnableWebSecurity
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = "/email")
    public ResponseEntity<String> updateEmail(@RequestBody EmailChange emailChange) {
        return userService.updateEmail(emailChange);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = "/username")
    public ResponseEntity<String> updateName(@RequestBody NameChange nameChange) {
        return userService.updateName(nameChange);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path = "/password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordChange passwordChange) {
        return userService.updatePassword(passwordChange);
    }
}
