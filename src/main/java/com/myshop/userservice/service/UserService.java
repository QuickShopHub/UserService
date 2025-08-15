package com.myshop.userservice.service;

import com.myshop.userservice.DTO.*;
import com.myshop.userservice.repository.User;
import com.myshop.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;



    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int addUser(UserDTO newUser) {
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        user.setEmail(newUser.getEmail());
        user.setId(UUID.randomUUID());
        user.setCreatedAt(LocalDate.now());
        user.setAdmin(newUser.isAdmin());

        if(userRepository.findAllByEmail(newUser.getEmail()) != 0){
            return 1;
        }

        userRepository.save(user);
        return 0;
    }



    public ResponseEntity<String> updateEmail(EmailChange emailChange){

        if(userRepository.findAllByEmail(emailChange.getNewEmail()) != 0){
            return ResponseEntity.badRequest().body("Email уже существует");
        }

        User user = userRepository.findByEmailAndPassword(emailChange.getEmail(), emailChange.getPassword());
        if(user == null){
            return ResponseEntity.badRequest().body("Неверный пароль");
        }


        user.setEmail(emailChange.getNewEmail());
        userRepository.save(user);
        return ResponseEntity.ok("Email updated");
    }

    public ResponseEntity<String> updateName(NameChange nameChange){
        User user = userRepository.findByEmailAndPassword(nameChange.getEmail(), nameChange.getPassword());
        if(user == null){
            return ResponseEntity.badRequest().body("Неверный пароль");
        }
        user.setUsername(nameChange.getUsername());
        userRepository.save(user);
        return ResponseEntity.ok("UserName updated");
    }

    public ResponseEntity<String> updatePassword(PasswordChange passwordChange){

        User user = userRepository.findByEmailAndPassword(passwordChange.getEmail(), passwordChange.getPassword());
        if(user == null){
            return ResponseEntity.badRequest().body("Неверный пароль");
        }
        user.setPassword(passwordChange.getNewPassword());


        userRepository.save(user);
        return ResponseEntity.ok("Password updated");
    }


}
