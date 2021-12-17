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

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<UserCreationDTO> createUser(@RequestBody UserCreationDTO userCreationDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(userCreationDTO));
    }
}
