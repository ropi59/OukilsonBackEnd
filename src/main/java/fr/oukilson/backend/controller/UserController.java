package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.UserDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * find user's profil by is nickname
     * @param nickname is the user's name
     * @return userDTO
     */
    @GetMapping("/{nickname}")
    public ResponseEntity<UserDTO> findUserByNickname(@PathVariable String nickname){
        UserDTO userDTO = userService.findUserByNickname(nickname);
        if(userDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(userDTO);
    }
}
