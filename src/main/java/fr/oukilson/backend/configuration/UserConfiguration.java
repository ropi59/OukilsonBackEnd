package fr.oukilson.backend.configuration;

import fr.oukilson.backend.repository.UserRepository;
import fr.oukilson.backend.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class UserConfiguration {

    /*
    create a dotenv object to retrieve the environmental variables
     */
    Dotenv dotenv = Dotenv.load();

    /*
    create a service bean to use inside the user side of the project
     */
    @Bean
    public UserService userService(UserRepository userRepository, ModelMapper modelMapper,
                                   @Qualifier("emailPattern") Pattern emailPattern,
                                   @Qualifier("nicknamePattern") Pattern nicknamePattern,
                                   @Qualifier("namePattern") Pattern namePattern) {
        return new UserService(userRepository, modelMapper, emailPattern, nicknamePattern, namePattern);
    }

    @Bean
    public Pattern emailPattern(){
        return Pattern.compile(dotenv.get("EMAIL_REGEX"));
    }

    @Bean
    public Pattern nicknamePattern(){
        return Pattern.compile(dotenv.get("NICKNAME_REGEX"));
    }

    @Bean
    public Pattern namePattern(){
        return Pattern.compile(dotenv.get("NAME_REGEX"));
    }
}
