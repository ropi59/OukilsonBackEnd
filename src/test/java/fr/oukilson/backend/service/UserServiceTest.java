package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.ResponseDTO;
import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.dto.UserDTO;
import fr.oukilson.backend.entity.RegexCollection;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.*;

import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    public User otherUser(){
        return new User(2L, "password2", "hello2@email.com", "nickname2");
    }

    public UserDTO userDTO() {
        return new UserDTO(1L, "hello@example.com", "nickname");
    }

    public UserDTO otherUserDTO() {
        return new UserDTO(2L, "hello2@email.com", "nickname2");
    }
    public ResponseDTO responseDTO(){
        return new ResponseDTO(true, "success");
    }

    public ResponseDTO responseDTOInvalid(){
        return new ResponseDTO(false, "failure");
    }

    /**
     * tests the create user function
     * create a response to compare the result to
     * tell the test what to expect and verify the result
     */
    @Test
    public void testCreateUser_AssertTrue() {
        when(userRepository.save(any(User.class))).thenReturn(user());
        ResponseDTO newResponseDTO = userService.createUser(userCreationDTO());
        Assertions.assertTrue(newResponseDTO.isSuccess());
//        verify(userRepository).save(user());
    }

    @Test
    public void testCreateUser_AssertFalse(){
        when(userRepository.save(any(User.class))).thenReturn(user());
        ResponseDTO newResponseDTO = userService.createUser(userCreationDTOInvalid());
        Assertions.assertFalse(newResponseDTO.isSuccess());
    }

    @Test
    public void testCreateUser(){
        when(userRepository.save(any(User.class))).thenReturn(user());
        userService.createUser(userCreationDTO());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void testAddUserToFriendList(){
        ResponseDTO responseDTO = responseDTOInvalid();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser()));
        Optional<User> mainUser = this.userRepository.findById(1L);
        Optional<User> otherUser = this.userRepository.findById(2L);

        if(mainUser.isEmpty() || otherUser.isEmpty())
            responseDTO.setMessage("empty return");
        else if(mainUser.equals(otherUser))
            responseDTO.setMessage("adding user to user");
        else{
            if(!mainUser.get().getFriendList().contains(otherUser.get())){
                mainUser.get().getFriendList().add(otherUser.get());
                responseDTO.setSuccess(true);
            }
        }
        Assertions.assertTrue(responseDTO.isSuccess());
        Assertions.assertTrue(mainUser.get().getFriendList().size() > 0);
    }

    @Test
    public void testAddUserToFriendList_mainUserEmpty(){
            ResponseDTO responseDTO = responseDTOInvalid();
            when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
            when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser()));
            Optional<User> mainUser = this.userRepository.findById(1L);
            Optional<User> otherUser = this.userRepository.findById(2L);
            if(mainUser.isEmpty() || otherUser.isEmpty()) {
                responseDTO.setMessage("empty return");
            }
            else if(mainUser.equals(otherUser)) {
                responseDTO.setMessage("adding user to user");
            }
            else{
                if(!mainUser.get().getFriendList().contains(otherUser.get())){
                    mainUser.get().getFriendList().add(otherUser.get());
                    responseDTO.setSuccess(true);
                }
            }
            Assertions.assertEquals(responseDTO.getMessage(), "empty return");
    }

    @Test
    public void testAddUserToFriendList_otherUserEmpty(){
        ResponseDTO responseDTO = responseDTOInvalid();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        Optional<User> mainUser = this.userRepository.findById(1L);
        Optional<User> otherUser = this.userRepository.findById(2L);
        if(mainUser.isEmpty() || otherUser.isEmpty()) {
            responseDTO.setMessage("empty return");
        }
        else if(mainUser.equals(otherUser)) {
            responseDTO.setMessage("adding user to user");
        }
        else{
            if(!mainUser.get().getFriendList().contains(otherUser.get())){
                mainUser.get().getFriendList().add(otherUser.get());
                responseDTO.setSuccess(true);
            }
        }
        Assertions.assertEquals(responseDTO.getMessage(), "empty return");
    }

    @Test
    public void testAddUserToFriendList_mainUserEqualsOtherUser(){
        ResponseDTO responseDTO = responseDTOInvalid();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user()));
        Optional<User> mainUser = this.userRepository.findById(1L);
        Optional<User> otherUser = this.userRepository.findById(2L);
        if(mainUser.isEmpty() || otherUser.isEmpty()) {
            responseDTO.setMessage("empty return");
        }
        else if(mainUser.equals(otherUser)) {
            responseDTO.setMessage("adding user to user");
        }
        else{
            if(!mainUser.get().getFriendList().contains(otherUser.get())){
                mainUser.get().getFriendList().add(otherUser.get());
                responseDTO.setSuccess(true);
            }
        }
        Assertions.assertEquals(responseDTO.getMessage(), "adding user to user");
    }

    @Test
    public void testAddUserToFriendList_otherUserAlreadyOnList(){
        ResponseDTO responseDTO = new ResponseDTO(false, "user already on list");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser()));
        Optional<User> mainUser = this.userRepository.findById(1L);
        Optional<User> otherUser = this.userRepository.findById(2L);
        if (mainUser.isPresent() && otherUser.isPresent())
            mainUser.get().getFriendList().add(otherUser.get());
        if(mainUser.isEmpty() || otherUser.isEmpty()) {
            responseDTO.setMessage("empty return");
        }
        else if(mainUser.equals(otherUser)) {
            responseDTO.setMessage("adding user to user");
        }
        else{
            if(!mainUser.get().getFriendList().contains(otherUser.get())){
                mainUser.get().getFriendList().add(otherUser.get());
                responseDTO.setSuccess(true);
            }
        }
        Assertions.assertEquals(responseDTO.getMessage(), "user already on list");
    }

    // TESTING REMOVING A USER FROM ANOTHER USER'S LIST //
    @Test
    public void testRemoveUserFromFriendList(){
        ResponseDTO responseDTO = responseDTOInvalid();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser()));
        Optional<User> mainUser = this.userRepository.findById(1L);
        Optional<User> otherUser = this.userRepository.findById(2L);
        if (mainUser.isPresent() && otherUser.isPresent())
            mainUser.get().getFriendList().add(otherUser.get());
        if (mainUser.isEmpty() || otherUser.isEmpty())
            responseDTO.setMessage("User not found");
        else {
            if (mainUser.get().getFriendList().contains(otherUser.get())) {
                mainUser.get().getFriendList().remove(otherUser.get());
                responseDTO.setSuccess(true);
                responseDTO.setMessage("User was successfully removed from list");
            }
        }
        Assertions.assertTrue(responseDTO.isSuccess());
        Assertions.assertEquals(0, mainUser.get().getFriendList().size());
    }

    @Test
    public void testRemoveUserFromFriendList_mainUserEmpty(){
        ResponseDTO responseDTO = responseDTOInvalid();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser()));
        Optional<User> mainUser = this.userRepository.findById(1L);
        Optional<User> otherUser = this.userRepository.findById(2L);
        if (mainUser.isEmpty() || otherUser.isEmpty())
            responseDTO.setMessage("User not found");
        else {
            if (mainUser.get().getFriendList().contains(otherUser.get())) {
                mainUser.get().getFriendList().remove(otherUser.get());
                this.userRepository.save(this.modelMapper.map(mainUser.get(), User.class));
                responseDTO.setSuccess(true);
                responseDTO.setMessage("User was successfully removed from list");
            }
        }
        Assertions.assertEquals(responseDTO.getMessage(), "User not found");
    }

    @Test
    public void testRemoveUserFromFriendList_otherUserEmpty(){
        ResponseDTO responseDTO = responseDTOInvalid();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        Optional<User> mainUser = this.userRepository.findById(1L);
        Optional<User> otherUser = this.userRepository.findById(2L);
        if (mainUser.isEmpty() || otherUser.isEmpty())
            responseDTO.setMessage("User not found");
        else {
            if (mainUser.get().getFriendList().contains(otherUser.get())) {
                mainUser.get().getFriendList().remove(otherUser.get());
                this.userRepository.save(this.modelMapper.map(mainUser.get(), User.class));
                responseDTO.setSuccess(true);
                responseDTO.setMessage("User was successfully removed from list");
            }
        }
        Assertions.assertEquals(responseDTO.getMessage(), "User not found");
    }

    @Test
    public void testRemoveUserFromFriendList_otherUserNotOnList(){
        ResponseDTO responseDTO = new ResponseDTO(false, "user not on list");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser()));
        Optional<User> mainUser = this.userRepository.findById(1L);
        Optional<User> otherUser = this.userRepository.findById(2L);
        if (mainUser.isEmpty() || otherUser.isEmpty())
            responseDTO.setMessage("User not found");
        else {
            if (mainUser.get().getFriendList().contains(otherUser.get())) {
                mainUser.get().getFriendList().remove(otherUser.get());
                this.userRepository.save(this.modelMapper.map(mainUser.get(), User.class));
                responseDTO.setSuccess(true);
                responseDTO.setMessage("User was successfully removed from list");
            }
        }
        Assertions.assertEquals(responseDTO.getMessage(), "user not on list");
    }


    // TESTING EMPTYING A USER'S FRIENDLIST //

    @Test
    public void testEmptyFriendList(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        Optional<User> mainUser = this.userRepository.findById(1L);
        ResponseDTO responseDTO = responseDTOInvalid();
        mainUser.ifPresent(user -> user.getFriendList().add(otherUser()));
        if(mainUser.isEmpty())
            responseDTO.setMessage("user not found");
        else {
            Iterator<User> iterator = mainUser.get().getFriendList().iterator();
            while(iterator.hasNext()){
                iterator.next();
                iterator.remove();
            }
            responseDTO.setMessage("success");
            responseDTO.setSuccess(true);
        }
        Assertions.assertTrue(responseDTO.isSuccess());
        Assertions.assertEquals(responseDTO.getMessage(), "success");
        Assertions.assertEquals(0, mainUser.get().getFriendList().size());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testEmptyFriendList_userNotFound(){
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        Optional<User> mainUser = this.userRepository.findById(1L);
        ResponseDTO responseDTO = responseDTOInvalid();
        if(mainUser.isEmpty())
            responseDTO.setMessage("user not found");
        else {
            Iterator<User> iterator = mainUser.get().getFriendList().iterator();
            while(iterator.hasNext()){
                iterator.next();
                iterator.remove();
            }
            responseDTO.setMessage("success");
            responseDTO.setSuccess(true);
        }
        Assertions.assertEquals(responseDTO.getMessage(), "user not found");
    }


    /**
     * test email regex
     */
    @Test
    @DisplayName("testing email checking method")
    public void emailIsValidAssertTrue(){
        String emailRegex = "(([^<>()\\[\\]\\\\.,;:\\s@\"]" +
                "+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@" +
                "((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";
        String[] emails = {"jeanpierre@email.com", ".j@e.c", "jeanpierre@yahoo.com"};
        Assertions.assertTrue(Pattern.compile(emailRegex).matcher(emails[0]).find());
        Assertions.assertFalse(Pattern.compile(emailRegex).matcher(emails[1]).find());
        Assertions.assertTrue(Pattern.compile(emailRegex).matcher(user().getEmail()).find());
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
