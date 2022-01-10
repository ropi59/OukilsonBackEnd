package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.UserEditDTO;
import fr.oukilson.backend.dto.UserNameDTO;
import fr.oukilson.backend.dto.UserProfilDTO;

import fr.oukilson.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
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

    /**
     * method to add icon to user profil
     * @param icon a picture selected by user
     * @return stock icon in DB
     */
    @PostMapping("/uploadFile")
    public ResponseEntity uploadFile(@RequestBody UserNameDTO userNameDTO, @RequestParam("file") MultipartFile icon){

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.storeFile(userNameDTO, icon));

    }

    /**
     * display picture in user profil
     * @param fileName name of the picture
     * @param request servlet to dld picture
     * @return the picture saved by user
     */
    @GetMapping("/icons/{fileName:.+}")
    public ResponseEntity<Resource> downloadIcon(@PathVariable String fileName, HttpServletRequest request){
        Resource resource = userService.loadIconAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e){
            logger.info("Couldn't determine file type.");
        }

        // contentType definition if not exist
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
