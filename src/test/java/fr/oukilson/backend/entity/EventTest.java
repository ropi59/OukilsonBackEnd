package fr.oukilson.backend.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventTest {
    /**
     * Create a valid User entity with all attributes set to valid data.
     * @param id User's id in database
     * @param nickname User's unique nickname
     * @return User
     */
    private User createValidFullUser(Long id, String nickname) {
        User user = new User();
        user.setId(id);
        user.setNickname(nickname);
        user.setPassword("d1e8a70b5ccab1dc2f56bbf7e99f064a660c08e361a35751b9c483c88943d082");
        user.setEmail("email@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }

    /**
     * Test the method addUser when adding a user in the empty registered list
     */
    @DisplayName("Test addUser : add a valid user in an empty registered list")
    @Test
    public void testAddUserWhenRegisteredListIsEmpty() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");

        Assertions.assertNotNull(event.getRegisteredUsers());
        Assertions.assertEquals(0, event.getRegisteredUsers().size());
        Assertions.assertTrue(event.addUser(user));
        Assertions.assertEquals(1, event.getRegisteredUsers().size());
        Assertions.assertEquals(user, event.getRegisteredUsers().get(0));
    }

    /**
     * Test the method addUser when adding a user in the registered list
     */
    @DisplayName("Test addUser : add a valid user in registered list")
    @Test
    public void testAddUser() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user1 = this.createValidFullUser(1L, "toto");
        User user2 = this.createValidFullUser(2L, "tata");

        Assertions.assertNotNull(event.getRegisteredUsers());
        Assertions.assertEquals(0, event.getRegisteredUsers().size());
        Assertions.assertTrue(event.addUser(user1));
        Assertions.assertEquals(1, event.getRegisteredUsers().size());
        Assertions.assertEquals(user1, event.getRegisteredUsers().get(0));
        Assertions.assertTrue(event.addUser(user2));
        Assertions.assertEquals(2, event.getRegisteredUsers().size());
        Assertions.assertEquals(user2, event.getRegisteredUsers().get(1));
    }

    /**
     * Test the method addUser when adding a user if the registered list is full
     */
    @DisplayName("Test addUser : add user in a full registered list")
    @Test
    public void testAddUserWhenListIsFull() {
        Event event = new Event();
        event.setMaxPlayer(2);
        event.addUser(this.createValidFullUser(1L, "toto"));
        event.addUser(this.createValidFullUser(2L, "tata"));
        Assertions.assertEquals(event.getMaxPlayer(), event.getRegisteredUsers().size());
        Assertions.assertFalse(event.addUser(this.createValidFullUser(3L, "alpha")));
    }

    /**
     * Test the method addUser when adding a user who is already in the waiting list
     */
    @DisplayName("Test addUser : add user who's already in waiting list")
    @Test
    public void testAddUserWhenUserIsAlreadyInWaitingList() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");

        Assertions.assertTrue(event.addUserInWaitingQueue(user));
        Assertions.assertFalse(event.addUser(user));
    }

    /**
     * Test the method addUser when adding a user who's already in the registered list
     */
    @DisplayName("Test addUser : add user who's already in registered list")
    @Test
    public void testAddUserWhenUserIsAlreadyInRegisteredList() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");

        Assertions.assertTrue(event.addUser(user));
        Assertions.assertFalse(event.addUser(user));
    }

    /**
     * Test the method addUserInWaitingQueue when adding a user in the empty waiting list
     */
    @DisplayName("Test addUserInWaitingQueue : add a valid user in an empty waiting list")
    @Test
    public void testAddUserInWaitingListWhenEmpty() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");

        Assertions.assertNotNull(event.getWaitingUsers());
        Assertions.assertEquals(0, event.getWaitingUsers().size());
        Assertions.assertTrue(event.addUserInWaitingQueue(user));
        Assertions.assertEquals(1, event.getWaitingUsers().size());
        Assertions.assertEquals(user, event.getWaitingUsers().get(0));
    }

    /**
     * Test the method addUserInWaitingQueue when adding a user in the waiting list
     */
    @DisplayName("Test addUserInWaitingQueue : add a valid user in the waiting list")
    @Test
    public void testAddUserInWaitingQueue() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user1 = this.createValidFullUser(1L, "toto");
        User user2 = this.createValidFullUser(2L, "tata");

        Assertions.assertNotNull(event.getWaitingUsers());
        Assertions.assertEquals(0, event.getWaitingUsers().size());
        Assertions.assertTrue(event.addUserInWaitingQueue(user1));
        Assertions.assertEquals(1, event.getWaitingUsers().size());
        Assertions.assertEquals(user1, event.getWaitingUsers().get(0));
        Assertions.assertTrue(event.addUserInWaitingQueue(user2));
        Assertions.assertEquals(2, event.getWaitingUsers().size());
        Assertions.assertEquals(user2, event.getWaitingUsers().get(1));
    }

    /**
     * Test the method addUserInWaitingQueue when adding a user in a full waiting list
     */
    @DisplayName("Test addUserInWaitingQueue : add a valid user in a full waiting list")
    @Test
    public void testAddUserInWaitingQueueWhenListIsFull() {
        Event event = new Event();
        event.setMaxPlayer(2);
        event.addUserInWaitingQueue(this.createValidFullUser(1L, "toto"));
        event.addUserInWaitingQueue(this.createValidFullUser(2L, "tata"));
        Assertions.assertEquals(event.getMaxPlayer(), event.getWaitingUsers().size());
        Assertions.assertFalse(event.addUserInWaitingQueue(this.createValidFullUser(3L, "alpha")));
    }

    /**
     * Test the method addUserInWaitingQueue when adding a user who's already in the waiting list
     */
    @DisplayName("Test addUserInWaitingQueue : add a valid user who's already in the waiting list")
    @Test
    public void testAddUserInWaitingQueueWhenUserIsAlreadyInWaitingList() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");

        Assertions.assertTrue(event.addUserInWaitingQueue(user));
        Assertions.assertFalse(event.addUserInWaitingQueue(user));
    }

    /**
     * Test the method addUserInWaitingQueue when adding a user who's already in the registered list
     */
    @DisplayName("Test addUserInWaitingQueue : add a valid user who's already in the registered list")
    @Test
    public void testAddUserInWaitingQueueWhenUserIsAlreadyInRegisteredList() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");

        Assertions.assertTrue(event.addUser(user));
        Assertions.assertFalse(event.addUserInWaitingQueue(user));
    }

    /**
     * Test the method removeUser
     */
    @DisplayName("Test removeUser : remove a user in the registered list")
    @Test
    public void testRemoveUser() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");

        event.addUser(user);
        Assertions.assertTrue(event.removeUser(user));
        Assertions.assertEquals(0, event.getRegisteredUsers().size());
    }

    /**
     * Test the method removeUser when removing a user in an empty registered list
     */
    @DisplayName("Test removeUser : remove a user when the registered list is empty")
    @Test
    public void testRemoveUserWithEmptyList() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");
        Assertions.assertFalse(event.removeUser(user));
    }

    /**
     * Test the method removeUser when removing a user who's not in the registered list
     */
    @DisplayName("Test removeUser : remove a user who's not in the registered list")
    @Test
    public void testRemoveUserWhoIsNotInList() {
        Event event = new Event();
        event.setMaxPlayer(5);
        event.addUser(this.createValidFullUser(1L, "toto"));
        Assertions.assertNotEquals(0, event.getRegisteredUsers().size());
        Assertions.assertFalse(event.removeUser(this.createValidFullUser(2L, "tata")));
    }

    /**
     * Test the method removeUserInWaitingQueue
     */
    @DisplayName("Test removeUserInWaitingQueue : remove a user in the waiting list")
    @Test
    public void testRemoveUserInWaitingQueue() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");

        event.addUserInWaitingQueue(user);
        Assertions.assertTrue(event.removeUserInWaitingQueue(user));
        Assertions.assertEquals(0, event.getWaitingUsers().size());
    }

    /**
     * Test the method removeUserInWaitingQueue on an empty waiting list
     */
    @DisplayName("Test removeUserInWaitingQueue : remove a user on an empty waiting list")
    @Test
    public void testRemoveUserInWaitingQueueWithEmptyList() {
        Event event = new Event();
        event.setMaxPlayer(5);
        User user = this.createValidFullUser(1L, "toto");
        Assertions.assertEquals(0, event.getWaitingUsers().size());
        Assertions.assertFalse(event.removeUserInWaitingQueue(user));
    }

    /**
     * Test the method removeUserInWaitingQueue when removing a user who's not in the waiting list
     */
    @DisplayName("Test removeUserInWaitingQueue : remove a user not in the waiting list")
    @Test
    public void testRemoveUserInWaitingQueueWhoIsNotInList() {
        Event event = new Event();
        event.setMaxPlayer(5);
        event.addUserInWaitingQueue(this.createValidFullUser(1L, "toto"));
        Assertions.assertFalse(event.removeUserInWaitingQueue(this.createValidFullUser(2L, "tata")));
    }
}
