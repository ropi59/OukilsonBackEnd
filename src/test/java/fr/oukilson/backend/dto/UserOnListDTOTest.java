package fr.oukilson.backend.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserOnListDTOTest {

    @Test
    public void testUserOnListCreation_EmailEquals(){
        UserOnListDTO userOnListDTO = new UserOnListDTO("email@email.com", "jean");
        String email = "email@email.com";
        Assertions.assertEquals(email, userOnListDTO.getEmail());
    }

    @Test
    public void testUserOnListCreation_EmailNotEquals(){
        UserOnListDTO userOnListDTO = new UserOnListDTO("email@email.com", "jean");
        String email = "emailz@email.com";
        Assertions.assertNotEquals(email, userOnListDTO.getEmail());
    }

    @Test
    public void testUserOnListCreation_NameEquals(){
        UserOnListDTO userOnListDTO = new UserOnListDTO("email@email.com", "jean");
        String name = "jean";
        Assertions.assertEquals(name, userOnListDTO.getNickname());
    }

    @Test
    public void testUserOnListCreation_NameNotEquals(){
        UserOnListDTO userOnListDTO = new UserOnListDTO("email@email.com", "jean");
        String name = "paul";
        Assertions.assertNotEquals(name, userOnListDTO.getNickname());
    }
}
