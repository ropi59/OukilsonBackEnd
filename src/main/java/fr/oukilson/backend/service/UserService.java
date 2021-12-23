package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.dto.CreationResponseDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class UserService {

    /*
     * declares the singleton repository as well as the mapper bean as attributes & injects them
     */
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private List<Pattern> regexCollection;


    public UserService(UserRepository userRepository, ModelMapper modelMapper,
                       List<Pattern> regexCollection) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.regexCollection = regexCollection;
    }

    /**
     * method to save a user entity to the database
     * @param userCreationDTO the DTO extracted from the body
     * @return a userCreationDTO
     */
    public CreationResponseDTO createUser(UserCreationDTO userCreationDTO) {
        // checks if input is valid then map into an entity to save it to the database
        if(this.regexCollection.get(0).matcher(userCreationDTO.getEmail()).find()
                && this.regexCollection.get(1).matcher(userCreationDTO.getNickname()).find()) {
            this.userRepository.save(this.modelMapper.map(userCreationDTO, User.class));
            return new CreationResponseDTO(true, "User was successfully created");
        }
        else {
            return new CreationResponseDTO(false, "User was not created");
        }
    }
}
