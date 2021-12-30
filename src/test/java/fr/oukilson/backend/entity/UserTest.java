package fr.oukilson.backend.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;


public class UserTest {

    public User user1(){
        return new User(1L, "password1", "email1@email.com", "username1");
    }

    public User user2(){
        return new User(2L, "password2", "email2@email.com", "username2");
    }


}
