package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.dto.UserDTO;
import fr.oukilson.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Method to save a user in the database
     * @param userCreationDTO the user object to be saved
     * @return ResponseEntity<UserDTO>
     */
    @PostMapping()
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreationDTO userCreationDTO){
        ResponseEntity<UserDTO> result;
        try {
            UserDTO event = this.userService.createUser(userCreationDTO);
            if (event!=null)
                result = ResponseEntity.status(HttpStatus.CREATED).body(event);
            else
                result = ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            result = ResponseEntity.badRequest().build();
        }
        return result;
    }

    /**
     * Add a user to another user's friend list, asking for both users' nickname
     * @param nickname1 Nickname of the main user
     * @param nickname2 Nickname of the friend to add
     * @return ResponseEntity<Boolean>
     */
    @PutMapping("/add/{id1}/{id2}")
    public ResponseEntity<Boolean> addUserToFriendList(@PathVariable(name = "id1") String nickname1,
                                                       @PathVariable(name = "id2") String nickname2) {
        return ResponseEntity.ok(this.userService.addUserToFriendList(nickname1, nickname2));
    }

    /**
     * Remove a user from another user's friend list
     * @param nickname1 Nickname of the main user
     * @param nickname2 Nickname of the friend to remove
     * @return ResponseEntity<Boolean>
     */
    @PutMapping("/remove/{id1}/{id2}")
    public ResponseEntity<Boolean> removeUserFromFriendList(@PathVariable(name = "id1") String nickname1,
                                                                @PathVariable(name = "id2") String nickname2) {
        return ResponseEntity.ok(this.userService.removeUserFromFriendList(nickname1, nickname2));
    }

    /**
     * Empties a user's friend list
     * @param nickname User's nickname
     * @return ResponseEntity<Boolean>
     */
    @PutMapping("/empty/{id}")
    public ResponseEntity<Boolean> emptyFriendList(@PathVariable(name = "id") String nickname) {
        return ResponseEntity.ok(this.userService.emptyFriendList(nickname));
    }
}
