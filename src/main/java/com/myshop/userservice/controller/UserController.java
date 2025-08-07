package com.myshop.userservice.controller;

import com.myshop.userservice.DTO.*;
import com.myshop.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @PutMapping(path = "/email")
    public ResponseEntity<String> updateEmail(@RequestBody EmailChange emailChange) {
        return userService.updateEmail(emailChange);
    }


    @PutMapping(path = "/username")
    public ResponseEntity<String> updateName(@RequestBody NameChange nameChange) {
        return userService.updateName(nameChange);
    }


    @PutMapping(path = "/password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordChange passwordChange) {
        return userService.updatePassword(passwordChange);
    }
}
