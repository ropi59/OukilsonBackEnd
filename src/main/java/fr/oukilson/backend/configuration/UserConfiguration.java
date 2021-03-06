package fr.oukilson.backend.configuration;

import fr.oukilson.backend.model.RegexCollection;
import fr.oukilson.backend.repository.UserRepository;
import fr.oukilson.backend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {
    @Bean
    public UserService userService(UserRepository userRepository, ModelMapper modelMapper,
                                   RegexCollection regexCollection){
        return new UserService(userRepository, modelMapper, regexCollection);
    }
}