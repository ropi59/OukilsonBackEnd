package fr.oukilson.backend.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class UserTest {

    @Test
    @DisplayName("test")
    public void testEmailSetterCorrectInput_ReturnTrue(){
        User user = new User();
        String email = "email@email.com";
        user.setEmail(email);
        Assertions.assertEquals(email, user.getEmail());
    }
}
