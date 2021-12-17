package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.UserDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private ModelMapper mapper;

    /**
     *  verify if user exist by nickname search
     * @param nickname username of user
     * @return userDTO
     */
    public UserDTO findUserByNickname(String nickname) {
        UserDTO userDTO = null;
        Optional<User> optionalUser = userRepository.findUserByNickname(nickname);
        if(optionalUser.isPresent()){
            User userFinded = optionalUser.get();
            userDTO = mapper.map(userFinded, UserDTO.class);
        }
        return userDTO;
    }
}
