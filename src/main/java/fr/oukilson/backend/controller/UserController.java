package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.UserEditDTO;
import fr.oukilson.backend.dto.UserListDTO;
import fr.oukilson.backend.dto.UserProfilDTO;
import fr.oukilson.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * get list of all users
     * @return
     */
    @GetMapping
    public List<UserListDTO> findAll(){
        return this.userService.findAll();
    }

    /**
     * find user's profil by is nickname
     * @param nickname is the user's name
     * @return userDTO
     */
    @GetMapping("{nickname}")
    public ResponseEntity<UserEditDTO> findUserByNickname(@PathVariable String nickname) {
        try {
            Optional<UserEditDTO> userDTO = this.userService.findUserByNickname(nickname);
            return ResponseEntity.ok(userDTO.get());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    /**
     * save user in BDD
     * @param userEditDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<UserEditDTO> save (@RequestBody UserEditDTO userEditDTO){
        UserEditDTO userSaved = this.userService.save(userEditDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userSaved);
    }

    /**
     * Edit user in BDD
     * @param userEditDTO
     * @return
     */
    @PutMapping
    public ResponseEntity<UserEditDTO> update (@RequestBody UserEditDTO userEditDTO){
        UserEditDTO userSaved = this.userService.save(userEditDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userSaved);
    }

    /**
     * delete user in BDD
     * @param userProfilDTO
     * @return
     */
    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody UserProfilDTO userProfilDTO){
        this.userService.delete(userProfilDTO);
        return ResponseEntity.ok(true);
    }

}
