package fr.oukilson.backend.service;

import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.model.RegexCollection;
import fr.oukilson.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private RegexCollection regexCollection;
    private UserService service;

    @BeforeAll
    public void init() {
        service = new UserService(userRepository, new ModelMapper(), regexCollection);
    }

    // Method createUser




    // Method addUserToFriendList




    // Method removeUserFromFriendList

    /**
     * Test removeUserFromFriendList when mainUser is null
     */
    @DisplayName("Test removeUserFromFriendList : mainUser is null")
    @Test
    public void testRemoveUserFromFriendListNullMainUser() {
        BDDMockito.when(userRepository.findByNickname(null)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> this.service.removeUserFromFriendList(null, "Machin"));
    }

    /**
     * Test removeUserFromFriendList when secondUser is null
     */
    @DisplayName("Test removeUserFromFriendList : secondUser is null")
    @Test
    public void testRemoveUserFromFriendListNullSecondUser() {
        User mainUser = new User();
        mainUser.setNickname("Pouic");
        BDDMockito.when(userRepository.findByNickname(mainUser.getNickname())).thenReturn(Optional.of(mainUser));
        BDDMockito.when(userRepository.findByNickname(null)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> this.service.removeUserFromFriendList(mainUser.getNickname(), null));
    }

    /**
     * Test removeUserFromFriendList when mainUser is not found
     */
    @DisplayName("Test removeUserFromFriendList : mainUser is not found")
    @Test
    public void testRemoveUserFromFriendListMainUserNotFound() {
        String nickname1 = "Radio";
        String nickname2 = "Gaga";
        BDDMockito.when(userRepository.findByNickname(nickname1)).thenReturn(Optional.empty());
        Assertions.assertFalse(this.service.removeUserFromFriendList(nickname1, nickname2));
    }

    /**
     * Test removeUserFromFriendList when secondUser is not found
     */
    @DisplayName("Test removeUserFromFriendList : secondUser is not found")
    @Test
    public void testRemoveUserFromFriendListSecondUserNotFound() {
        String nickname1 = "Radio";
        String nickname2 = "Gaga";
        User user = new User();
        user.setNickname(nickname1);
        BDDMockito.when(userRepository.findByNickname(nickname1)).thenReturn(Optional.of(user));
        BDDMockito.when(userRepository.findByNickname(nickname2)).thenReturn(Optional.empty());
        Assertions.assertFalse(this.service.removeUserFromFriendList(nickname1, nickname2));
    }

    /**
     * Test removeUserFromFriendList when everything is ok
     */
    @DisplayName("Test removeUserFromFriendList : user removed")
    @Test
    public void testRemoveUserFromFriendListEverythingOk() {
        // Populate the friend list
        String nickname1 = "Radio";
        String nickname2 = "Gaga";
        User user1 = new User();
        user1.setNickname(nickname1);
        User user2 = new User();
        user2.setNickname(nickname2);
        user2.setId(1000L);
        user1.getFriendList().add(user2);
        int size = 3;
        for (int i=0; i<size; i++) {
            User temp = new User();
            temp.setId((long)i);
            temp.setNickname("User "+i);
            user1.getFriendList().add(temp);
        }

        // Mock and assert
        BDDMockito.when(userRepository.findByNickname(nickname1)).thenReturn(Optional.of(user1));
        BDDMockito.when(userRepository.findByNickname(nickname2)).thenReturn(Optional.of(user2));
        Assertions.assertEquals(size+1, user1.getFriendList().size());
        Assertions.assertTrue(user1.getFriendList().contains(user2));
        Assertions.assertTrue(this.service.removeUserFromFriendList(nickname1, nickname2));
        Assertions.assertEquals(size, user1.getFriendList().size());
        Assertions.assertFalse(user1.getFriendList().contains(user2));
    }

    // Method emptyFriendList

    /**
     * Test emptyFriendList with null nickname
     */
    @DisplayName("Test emptyFriendList : null nickname")
    @Test
    public void testEmptyFriendListNullNickname() {
        BDDMockito.when(this.userRepository.findByNickname(null)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> this.service.emptyFriendList(null));
    }

    /**
     * Test emptyFriendList when user is found
     */
    @DisplayName("Test emptyFriendList : user not found")
    @Test
    public void testEmptyFriendListUserIsFound() {
        String nickname = "Alpha";
        BDDMockito.when(this.userRepository.findByNickname(nickname)).thenReturn(Optional.empty());
        Assertions.assertFalse(this.service.emptyFriendList(nickname));
    }

    /**
     * Test emptyFriendList when user is not found
     */
    @DisplayName("Test emptyFriendList : user found, empty successful")
    @Test
    public void testEmptyFriendListUserNotFound() {
        String nickname = "Alpha";
        User user = new User();
        user.setNickname(nickname);
        int size = 2;
        for (int i=0; i<size; i++) {
            User temp = new User();
            temp.setId((long)i);
            temp.setNickname("User "+i);
            user.getFriendList().add(temp);
        }
        BDDMockito.when(this.userRepository.findByNickname(nickname)).thenReturn(Optional.of(user));
        Assertions.assertEquals(size, user.getFriendList().size());
        Assertions.assertTrue(this.service.emptyFriendList(nickname));
        Assertions.assertEquals(0, user.getFriendList().size());
    }
}
