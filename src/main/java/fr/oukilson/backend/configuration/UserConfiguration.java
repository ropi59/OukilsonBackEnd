package fr.oukilson.backend.configuration;

import fr.oukilson.backend.repository.UserRepository;
import fr.oukilson.backend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.regex.Pattern;


@Configuration
public class UserConfiguration {

    /*
    create a service bean to use inside the user side of the project
     */
    @Bean
    public UserService userService(UserRepository userRepository, ModelMapper modelMapper,
                                   List<Pattern> regexCollection) {
        return new UserService(userRepository, modelMapper, regexCollection);
    }



}
