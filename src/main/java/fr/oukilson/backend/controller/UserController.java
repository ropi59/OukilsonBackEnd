package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    /*
    declares the beaned service as an attribute then inject it inside the constructor
     */
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * method to save a user in the database
     * @param userCreationDTO the user object to be saved
     * @return a response entity validating the created user
     */
    @PostMapping()
    public ResponseEntity<UserCreationDTO> createUser(@RequestBody UserCreationDTO userCreationDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(userCreationDTO));
    }
}
