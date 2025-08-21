package com.myshop.userservice.service;

import com.myshop.userservice.DTO.*;
import com.myshop.userservice.config.EmailEncryptionUtil;
import com.myshop.userservice.repository.User;
import com.myshop.userservice.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int addUser(UserDTO newUser) {
        User user = new User();

        user.setUsername(newUser.getUsername());

        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        try {
            user.setEmail(EmailEncryptionUtil.encodeEmail(newUser.getEmail()));
        }catch (Exception e){
            log.error(e.getMessage());
            return 2;
        }
        user.setId(UUID.randomUUID());
        user.setCreatedAt(LocalDate.now());
        user.setAdmin(newUser.isAdmin());



        if(userRepository.findAllByEmail(user.getEmail()) != 0){
            return 1;
        }
        userRepository.save(user);
        return 0;
    }



    public ResponseEntity<String> updateEmail(EmailChange emailChange){
        String emailCodeNew = null;
        String emailCodeLast = null;
        try {
            emailCodeNew = EmailEncryptionUtil.encodeEmail(emailChange.getNewEmail());
            emailCodeLast  = EmailEncryptionUtil.encodeEmail(emailChange.getEmail());
        }
        catch (Exception e){
            ResponseEntity.internalServerError().body("");
        }

        if(userRepository.findAllByEmail(emailCodeNew) != 0){
            return ResponseEntity.badRequest().body("Email уже существует");
        }


        User user = checkAuth(emailChange.getPassword(), emailChange.getEmail());
        if(user == null){
            return ResponseEntity.badRequest().body("Неверный пароль");
        }


        user.setEmail(emailCodeNew);
        userRepository.save(user);
        return ResponseEntity.ok("Email updated");
    }

    public ResponseEntity<String> updateName(NameChange nameChange){
        User user = checkAuth(nameChange.getPassword(), nameChange.getEmail());
        if(user == null){
            return ResponseEntity.badRequest().body("Неверный пароль");
        }
        user.setUsername(nameChange.getUsername());
        userRepository.save(user);
        return ResponseEntity.ok("UserName updated");
    }

    public ResponseEntity<String> updatePassword(PasswordChange passwordChange){


        User user = checkAuth(passwordChange.getPassword(), passwordChange.getEmail());

        if(user == null){
            return ResponseEntity.badRequest().body("Неверный пароль");
        }


        user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));


        userRepository.save(user);
        return ResponseEntity.ok("Password updated");
    }

    private User checkAuth(String password, String Email){
        try {
            String emailCode = EmailEncryptionUtil.encodeEmail(Email);
            User user = userRepository.findUserByEmail(emailCode);
            if(passwordEncoder.matches(password, user.getPassword())){
                return user;
            }
            else return null;
        }
        catch (Exception e){
            return null;
        }
    }


}
