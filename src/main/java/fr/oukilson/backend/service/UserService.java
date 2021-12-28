package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.dto.ResponseDTO;
import fr.oukilson.backend.dto.UserDTO;
import fr.oukilson.backend.entity.RegexCollection;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    /*
     * declares the singleton repository as well as the mapper bean as attributes & injects them
     */
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private RegexCollection regexCollection;


    public UserService(UserRepository userRepository, ModelMapper modelMapper, RegexCollection regexCollection) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.regexCollection = regexCollection;
    }

    /**
     * method to save a user entity to the database
     * @param userCreationDTO the DTO extracted from the body
     * @return a userCreationDTO
     */
    public ResponseDTO createUser(UserCreationDTO userCreationDTO) {
        // checks if input is valid then map into an entity to save it to the database
        System.out.println(regexCollection.getEmailPattern());
        if(this.regexCollection.getEmailPattern().matcher(userCreationDTO.getEmail()).find()
                && this.regexCollection.getNicknamePattern().matcher(userCreationDTO.getNickname()).find()) {
            this.userRepository.save(this.modelMapper.map(userCreationDTO, User.class));
            return new ResponseDTO(true, "User was successfully created");
        }
        else {
            return new ResponseDTO(false, "User was not created");
        }
    }

    public ResponseDTO addUserToFriendList(Long mainUserId, Long secondUserId) {
        Optional<User> mainUser = this.userRepository.findById(mainUserId);
        Optional<User> userToAdd = this.userRepository.findById(secondUserId);
        ResponseDTO responseDTO = new ResponseDTO(false, "User was already on list");
        if(mainUser.isPresent() && userToAdd.isPresent()){
        if (!mainUser.get().getFriendList().contains(userToAdd.get())) {
            mainUser.get().getFriendList().add(userToAdd.get());
            this.userRepository.save(this.modelMapper.map(mainUser.get(), User.class));
            responseDTO.setMessage("User was successfully added to the list");
            responseDTO.setSuccess(true);
        }
        }
        return responseDTO;
    }

    public UserDTO findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        UserDTO userDTO = null;
        if (user.isPresent())
            userDTO = this.modelMapper.map(user.get(), UserDTO.class);
        return userDTO;
    }

    public List<UserDTO> findAll() {
        List<UserDTO> list = new ArrayList<>();
        this.userRepository.findAll().forEach(user ->
            list.add(this.modelMapper.map(user, UserDTO.class)));
        return list;
    }
}
