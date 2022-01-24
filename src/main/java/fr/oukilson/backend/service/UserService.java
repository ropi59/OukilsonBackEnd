package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.user.UserDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.model.RegexCollection;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import java.util.Optional;

public class UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private RegexCollection regexCollection;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, RegexCollection regexCollection) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.regexCollection = regexCollection;
    }

    /**
     * Search a user by nickname
     * @param nickname User's nickname
     * @return UserDTO
     */
    public UserDTO findUserByNickname(String nickname) {
        UserDTO result;
        if (this.regexCollection.getNicknamePattern().matcher(nickname).find()) {
            Optional<User> optionalUser = userRepository.findByNickname(nickname);
            result = optionalUser.map(user -> this.modelMapper.map(user, UserDTO.class)).orElse(null);
        }
        else
            result = null;
        return result;
    }

}
