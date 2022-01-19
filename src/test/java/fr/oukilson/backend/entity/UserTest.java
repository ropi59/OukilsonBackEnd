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

    @Test
    public void testUserCreation_NameEquals() {
        User user = user1();
        String nickname = "username1";
        Assertions.assertEquals(user.getNickname(), nickname);
    }

    @Test
    public void testUserCreation_NameNotEquals() {
        User user = user1();
        String nickname = "username2";
        Assertions.assertNotEquals(user1().getNickname(), nickname);
    }

    @Test
    public void testUserCreation_PasswordEquals() {
        User user = user1();
        String password = "password1";
        Assertions.assertEquals(user.getPassword(), password);
    }

    @Test
    public void testUserCreation_PasswordNotEquals() {
        User user = user1();
        String password = "password2";
        Assertions.assertNotEquals(user1().getPassword(), password);
    }

    @Test
    public void testUserCreation_EmailEquals() {
        User user = user1();
        String email = "email1@email.com";
        Assertions.assertEquals(user.getEmail(), email);
    }

    @Test
    public void testUserCreation_EmailNotEquals() {
        User user = user1();
        String email = "email2@email.com";
        Assertions.assertNotEquals(user1().getEmail(), email);
    }

    @Test
    public void testUserCreation_FriendListAddedOK(){
        User user = user1();
        User user2 = user2();
        user.getFriendList().add(user2);
        Assertions.assertEquals(user.getFriendList().get(0), user2);
    }

    @Test
    public void testUserCreation_FriendListRemovedOK(){
        User user = user1();
        User user2 = user2();
        User user3 = new User(3L, "password3", "email3@email.com", "username3");
        user.getFriendList().add(user2);
        user.getFriendList().add(user3);
        user.getFriendList().remove(user2);
        Assertions.assertEquals(user.getFriendList().get(0), user3);
    }

    @Test
    public void testUserCreation_DeniedListAddedOK(){
        User user = user1();
        User user2 = user2();
        user.getDeniedList().add(user2);
        Assertions.assertEquals(user.getDeniedList().get(0), user2);
    }

    @Test
    public void testUserCreation_DeniedListRemovedOK(){
        User user = user1();
        User user2 = user2();
        User user3 = new User(3L, "password3", "email3@email.com", "username3");
        user.getDeniedList().add(user2);
        user.getDeniedList().add(user3);
        user.getDeniedList().remove(user2);
        Assertions.assertEquals(user.getDeniedList().get(0), user3);
    }
}
