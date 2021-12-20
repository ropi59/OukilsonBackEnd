package fr.oukilson.backend.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OukilsonConfiguration {

    /**
     * create a modelmapper bean to use from inside the project
     * @return a new modelmapper
     */
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
