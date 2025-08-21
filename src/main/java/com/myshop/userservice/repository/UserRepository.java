package com.myshop.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = "SELECT COUNT(*) FROM users WHERE key = :key",  nativeQuery = true)
    int findAllByKey(String key);


    @Query(value = "SELECT COUNT(*) FROM users WHERE email = :email",  nativeQuery = true)
    int findAllByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE email = :email AND password = :password",   nativeQuery = true)
    User findByEmailAndPassword(String email, String password);

    @Query(value = "SELECT * FROM users WHERE email=:email",  nativeQuery = true)
    User findUserByEmail(String email);

}
