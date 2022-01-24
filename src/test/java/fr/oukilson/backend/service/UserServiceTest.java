package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.user.UserDTO;
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

    // Method findUserByNickname

    /**
     * Test findUserByNickname when nickname is null
     */
    @DisplayName("Test findUserByNickname : nickname is null")
    @Test
    public void testFindUserByNicknameNull() {
        Assertions.assertThrows(NullPointerException.class, () -> this.service.findUserByNickname(null));
    }

    /**
     * Test findUserByNickname when nickname is invalid
     */
    @DisplayName("Test findUserByNickname : nickname is invalid")
    @Test
    public void testFindUserByNicknameInvalid() {
        String nickname = "Clément";
        Assertions.assertNull(this.service.findUserByNickname(nickname));
    }

    /**
     * Test findUserByNickname when nickname is valid but the user is not found
     */
    @DisplayName("Test findUserByNickname : user not found")
    @Test
    public void testFindUserByNicknameUserNotFound() {
        String nickname = "Popo";
        BDDMockito.when(this.userRepository.findByNickname(nickname)).thenReturn(Optional.empty());
        Assertions.assertNull(this.service.findUserByNickname(nickname));
    }

    /**
     * Test findUserByNickname when nickname is valid and the user is found
     */
    @DisplayName("Test findUserByNickname : user found")
    @Test
    public void testFindUserByNicknameUserFound() {
        User user = new User();
        user.setNickname("Bruce");
        user.setPassword("kljsgfsmirgu");
        user.setId(1L);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        UserDTO result = this.service.findUserByNickname(user.getNickname());
        Assertions.assertNotNull(result);
        ModelMapper mapper = new ModelMapper();
        Assertions.assertEquals(mapper.map(user, UserDTO.class), result);
    }
}
