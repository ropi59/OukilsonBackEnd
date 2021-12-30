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


    /*
    basic get methods for testing purposes
     */
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

    /**
     * adds a user to the main user's friend list
     * @param mainUserId Long
     * @param secondUserId Long
     * @return a response saying whether the user was successfully added or not
     */
    public ResponseDTO addUserToFriendList(Long mainUserId, Long secondUserId) {
        // attempts to find both users on the database
        Optional<User> mainUser = this.userRepository.findById(mainUserId);
        Optional<User> userToAdd = this.userRepository.findById(secondUserId);
        // create a default response
        ResponseDTO responseDTO = new ResponseDTO(false, "User was already on list");
        // check if users were found
        if (mainUser.isEmpty() || userToAdd.isEmpty()) {
            // modifies the response message
            responseDTO.setMessage("User(s) not found");
        }
        else {
            // checks if the user to add was already on the list
            if (!mainUser.get().getFriendList().contains(userToAdd.get())) {
                // adds it
                mainUser.get().getFriendList().add(userToAdd.get());
                // modifies the main user object so that it can be saved into the database
                // and modifies the response message accordingly
                this.userRepository.save(this.modelMapper.map(mainUser.get(), User.class));
                responseDTO.setMessage("User was successfully added to the list");
                responseDTO.setSuccess(true);
            }
        }
        return responseDTO;
    }

    /**
     * remove a user from a friend list
     * @param id1 Long
     * @param id2 Long
     * @return a DTO containing a boolean and a message
     */
    public ResponseDTO removeUserFromFriendList(Long id1, Long id2) {
        // attempts to find both users by id
        Optional<User> mainUser = this.userRepository.findById(id1);
        Optional<User> userToRemove = this.userRepository.findById(id2);
        // creates a default response
        ResponseDTO responseDTO = new ResponseDTO(false, "User was not found on list");
        // checks if users were found and modifies the message if needed
        if (mainUser.isEmpty() || userToRemove.isEmpty())
            responseDTO.setMessage("User not found");
        else {
            // check if second user is actually on the first user's friend list
            if (mainUser.get().getFriendList().contains(userToRemove.get())) {
                // removes unwanted user, maps main user so that it can be saved, modifies the response
                mainUser.get().getFriendList().remove(userToRemove.get());
                this.userRepository.save(this.modelMapper.map(mainUser.get(), User.class));
                responseDTO.setSuccess(true);
                responseDTO.setMessage("User was successfully removed from list");
            }
        }
        return responseDTO;
    }

    /**
     * function that empties a friend list
     * @param id Long
     * @return a response containing a boolean for success/failure and a message
     */
    public ResponseDTO emptyFriendList(Long id) {
        // attempts to find the user
        Optional<User> user = this.userRepository.findById(id);
        // creating a default response
        ResponseDTO response = new ResponseDTO(false, "Failed to empty list");
        // checks if the user was found and modifies the message accordingly
        if (user.isEmpty())
            response.setMessage("User not found");
        // empties the friend list of the user then saves the user into the database then modifies the response
        else {
            user.get().getFriendList().forEach(user1 ->
                    user.get().getFriendList().remove(user1));
            this.userRepository.save(this.modelMapper.map(user.get(), User.class));
            response.setSuccess(true);
            response.setMessage("List successfully emptied");
        }
        return response;
    }
}
