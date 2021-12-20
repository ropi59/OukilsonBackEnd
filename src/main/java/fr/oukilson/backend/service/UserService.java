package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.dto.CreationResponseDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.regex.Pattern;

public class UserService {

    /*
     * declares the singleton repository as well as the mapper bean as attributes & injects them
     */
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private Pattern emailPattern;
    private Pattern nicknamePattern;
    private Pattern namePattern;

    public UserService(UserRepository userRepository, ModelMapper modelMapper,
                       @Qualifier("emailPattern") Pattern emailPattern,
                       @Qualifier("nicknamePattern") Pattern nicknamePattern,
                       @Qualifier("namePattern") Pattern namePattern) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.emailPattern = emailPattern;
        this.nicknamePattern = nicknamePattern;
        this.namePattern = namePattern;
    }


    /**
     * checks the email's validity against a pre-established pattern
     * @param email String to check
     * @return true if valid
     */
    public boolean emailIsValid(String email){
        return emailPattern.matcher(email).find();
    }

    /**
     * checks the nickname's validity against a pre-established pattern
     * @param nickname String to check
     * @return true if valid
     */
    public boolean nicknameIsValid(String nickname){
        return nicknamePattern.matcher(nickname).find();
    }


    /**
     * method to save a user entity to the database
     * @param userCreationDTO the DTO extracted from the body
     * @return a userCreationDTO
     */
    public CreationResponseDTO createUser(UserCreationDTO userCreationDTO) {
        // creates a user to map usercreationdto into
        User user = null;
        // checks if input is valid then map into an entity to save it to the database
        if(emailPattern.matcher(userCreationDTO.getEmail()).find()
                && nicknamePattern.matcher(userCreationDTO.getNickname()).find()) {
            user = this.userRepository.save(this.modelMapper.map(userCreationDTO, User.class));
            return new CreationResponseDTO(true, "User was successfully created");
        }
        else {
            return new CreationResponseDTO(false, "User was not created");
        }
    }
}
