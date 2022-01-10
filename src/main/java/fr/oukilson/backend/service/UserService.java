package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.UserEditDTO;
import fr.oukilson.backend.dto.UserListDTO;
import fr.oukilson.backend.dto.UserNameDTO;
import fr.oukilson.backend.dto.UserProfilDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.exception.FileStorageException;
import fr.oukilson.backend.exception.MyFileNotFoundException;
import fr.oukilson.backend.payload.UploadFileResponse;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private UserRepository userRepository;
    private ModelMapper mapper;
    /**
     * Path definition
     */
    private Path fileStorageLocation;

    private String path;


    public UserService (UserRepository userRepository, ModelMapper mapper, String path){
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.path = path;
        System.out.println(this.path);
        //this.fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();

        /*
        try{
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception e){
            throw new FileStorageException("Could not create directory where uploaded files will be stored", e);
        }*/
    }

    /**
     *  verify if user exist by nickname search
     * @param nickname username of user
     * @return userDTO
     */
    public Optional<UserEditDTO> findUserByNickname(String nickname) {
        Optional<User> optionalUser = userRepository.findUserByNickname(nickname);
        return Optional.of(mapper.map(optionalUser.get(), UserEditDTO.class));
    }

    /**
     * get a list of all users
     * @return
     */
    public List<UserListDTO> findAll(){
        List<UserListDTO> userListDTOList = new ArrayList<>();
        this.userRepository.findAll().forEach(user ->{
            userListDTOList.add(mapper.map(user, UserListDTO.class));
        });
        return userListDTOList;
    }

    /**
     * save or update user method
     * @param userEditDTO
     * @return
     */
    public UserEditDTO save (UserEditDTO userEditDTO){
        User user = mapper.map(userEditDTO, User.class);
        User userToSave = this.userRepository.save(user);
        UserEditDTO userSaved = mapper.map(userToSave, UserEditDTO.class);
        return userSaved;
    }

    /**
     * delete user method
     * @param userProfilDTO
     */
    public void delete(UserProfilDTO userProfilDTO){
        User user = mapper.map(userProfilDTO, User.class);
        this.userRepository.delete(user);
    }

    /**
     * save file on server method
     * @param icon
     * @return
     */
    public UploadFileResponse storeFile(UserNameDTO userNameDTO, MultipartFile icon)
            throws FileStorageException {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();

        //search user by nickname
        Optional<User> optionalUser = userRepository.findUserByNickname(userNameDTO.getNickname());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            //file name normalization
            String fileName = StringUtils.cleanPath(icon.getOriginalFilename());

            try {
                //check if file's name contains invalid characters
                if (fileName.contains("..")) {
                    throw new FileStorageException("Filename contains invalid characters " + fileName);
                }
                //copy file to the target location, replacing existing file with same name
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.copy(icon.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                uploadFileResponse = this.getUploadFileResponse(icon, fileName);
                user.setIconFilename(fileName);
                this.userRepository.save(user);
            }
            catch (IOException e) {
                throw new FileStorageException("Could not record file " + fileName + ". Please try again.", e);
            }
        }
        return uploadFileResponse;
    }


    /**
     * function to get icon on server
     * @param fileName
     * @return
     */
    public Resource loadIconAsResource(String fileName){
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()){
                return resource;
            }else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        }catch (MalformedURLException e){
            throw new MyFileNotFoundException("File not found " + fileName);
        }
    }

    /**
     * format UploadFileResponse function
     * @param icon
     * @param fileName
     * @return
     */
    private UploadFileResponse getUploadFileResponse(MultipartFile icon, String fileName){
        //uri definition for download
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        return new UploadFileResponse(fileName, fileDownloadUri);
    }

}
