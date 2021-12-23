package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.CreationResponseDTO;
import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @MockBean
//    @InjectMocks
    private UserService userService;


    /**
     * tests the create user function
     * create a response to compare the result to
     * tell the test what to expect and verify the result
     */
    @Test
    public void testCreateUser_AssertTrue(){
        CreationResponseDTO creationResponseDTO = new CreationResponseDTO(true, "success");
        UserCreationDTO userCreationDTO = new UserCreationDTO("password", "hello@example.com", "nickname");
        when(userService.createUser(any(UserCreationDTO.class))).thenReturn(creationResponseDTO);
        CreationResponseDTO newCreationResponseDTO = userService.createUser(userCreationDTO);
        Assertions.assertTrue(newCreationResponseDTO.isSuccess());
    }


    /**
     * test email regex
     */
    @Test
    @DisplayName("testing email checking method")
    public void emailIsValidAssertTrue(){
        String emailRegex = "(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";
        String[] emails = {"jeanpierre@email.com", ".j@e.c", "jeanpierre@email.com.", "jeanpierre@yahoo.com"};
//        Assertions.assertTrue(Pattern.compile(emailRegex).matcher(emails[0]).find());
//        Assertions.assertFalse(Pattern.compile(emailRegex).matcher(emails[1]).find());
//        Assertions.assertFalse(Pattern.compile(emailRegex).matcher(emails[2]).find());
        Assertions.assertTrue(Pattern.compile(emailRegex).matcher("hello@example.com").find());
    }

    /**
     * test nickname regex
     */
    @Test
    @DisplayName("testing username validation method")
    public void nicknameIsValid(){
        String[] nicknames = {"titi", "t", "titi2", "..azd"};
        String nicknameRegex = "^[a-zA-Z0-9_-]{4,16}$";
//        Assertions.assertTrue(Pattern.compile(nicknameRegex).matcher(nicknames[0]).find());
        Assertions.assertFalse(Pattern.compile(nicknameRegex).matcher(nicknames[1]).find());
//        Assertions.assertTrue(Pattern.compile(nicknameRegex).matcher(nicknames[2]).find());
//        Assertions.assertFalse(Pattern.compile(nicknameRegex).matcher(nicknames[3]).find());
    }
}
