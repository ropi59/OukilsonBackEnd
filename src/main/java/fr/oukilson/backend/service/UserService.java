package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

public class UserService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserCreationDTO createUser(UserCreationDTO userCreationDTO) {
        User user = this.userRepository.save(this.modelMapper.map(userCreationDTO, User.class));
        return this.modelMapper.map(user, UserCreationDTO.class);
    }
}
