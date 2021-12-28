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
    public void testAddUserToFriendList() throws Exception {
        User user1 = user1();
        User user2 = user2();
        user1.addUserToFriendList(user2);
        Assertions.assertEquals(user1.getFriendList().get(0), user2);
    }

    @Test
    public void testAddUserToFriendList_Exception() throws Exception {
        User user1 = user1();
        User user2 = user2();
        user1.addUserToFriendList(user2);
        Assertions.assertThrows(Exception.class, () ->
                user1.addUserToFriendList(user2));
    }

    @Test
    public void testRemoveUserFromFriendList() throws Exception {
        User user1 = user1();
        User user2 = user2();
        User user3 = new User(3L, "password3", "email3@email.com", "nickname3");
        user1.getFriendList().add(user2);
        user1.getFriendList().add(user3);
        user1.removeUserFromFriendList(user2);
        Assertions.assertEquals(user1.getFriendList().get(0), user3);
    }
}
