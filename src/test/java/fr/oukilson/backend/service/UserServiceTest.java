package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.CreationResponseDTO;
import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.entity.RegexCollection;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.junit4.SpringRunner;

import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    RegexCollection regexCollection;



    public UserCreationDTO userCreationDTO(){
        return new UserCreationDTO("password", "hello@example.com", "nickname");
    }

    public UserCreationDTO userCreationDTOInvalid(){
        return new UserCreationDTO("password", "e.com", "nickname");
    }

    public User user(){
        return new User(1L, "password", "hello@example.com", "nickname");
    }

    public CreationResponseDTO creationResponseDTO(){
        return new CreationResponseDTO(true, "User successfully created.");
    }

    public CreationResponseDTO creationResponseDTOInvalid(){
        return new CreationResponseDTO(false, "User creation failed.");
    }
    /**
     * tests the create user function
     * create a response to compare the result to
     * tell the test what to expect and verify the result
     */
    @Test
    public void testCreateUser_AssertTrue() {
        when(userRepository.save(any(User.class))).thenReturn(user());
        CreationResponseDTO newCreationResponseDTO = userService.createUser(userCreationDTO());
        Assertions.assertTrue(newCreationResponseDTO.isSuccess());
//        verify(userRepository).save(user());
    }

    @Test
    public void testCreateUser_AssertFalse(){
        when(userRepository.save(any(User.class))).thenReturn(user());
        CreationResponseDTO newCreationResponseDTO = userService.createUser(userCreationDTOInvalid());
        Assertions.assertFalse(newCreationResponseDTO.isSuccess());
    }


    /**
     * test email regex
     */
    @Test
    @DisplayName("testing email checking method")
    public void emailIsValidAssertTrue(){
        String emailRegex = "(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";
        String[] emails = {"jeanpierre@email.com", ".j@e.c", "jeanpierre@yahoo.com"};
        Assertions.assertTrue(Pattern.compile(emailRegex).matcher(emails[0]).find());
        Assertions.assertFalse(Pattern.compile(emailRegex).matcher(emails[1]).find());
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
        Assertions.assertTrue(Pattern.compile(nicknameRegex).matcher(nicknames[0]).find());
        Assertions.assertFalse(Pattern.compile(nicknameRegex).matcher(nicknames[1]).find());
        Assertions.assertTrue(Pattern.compile(nicknameRegex).matcher(nicknames[2]).find());
        Assertions.assertFalse(Pattern.compile(nicknameRegex).matcher(nicknames[3]).find());
    }
}
