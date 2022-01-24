package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.user.UserDTO;
import fr.oukilson.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * Find a user and send its info back
     * @param nickname User's nickname
     * @return UserDTO
     */
    @GetMapping("{nickname}")
    public ResponseEntity<UserDTO> findUserByNickname(@PathVariable String nickname) {
        ResponseEntity<UserDTO> result;
        try {
            UserDTO userDTO = this.userService.findUserByNickname(nickname);
            if (userDTO!=null)
                result = ResponseEntity.ok(userDTO);
            else
                result = ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            result = ResponseEntity.badRequest().build();
        }
        return result;
    }
}
