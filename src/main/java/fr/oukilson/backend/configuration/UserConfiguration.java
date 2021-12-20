package fr.oukilson.backend.configuration;

import fr.oukilson.backend.repository.UserRepository;
import fr.oukilson.backend.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class UserConfiguration {


    @Value("${environment.emailRegex}")
    private String emailRegex;

    @Value("${environment.nicknameRegex}")
    private String nicknameRegex;

    @Value("${environment.nameRegex}")
    private String nameRegex;

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
        return Pattern.compile(emailRegex);
    }

    @Bean
    public Pattern nicknamePattern(){
        return Pattern.compile(nicknameRegex);
    }

    @Bean
    public Pattern namePattern(){
        return Pattern.compile(nameRegex);
    }
}
