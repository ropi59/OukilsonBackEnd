package fr.oukilson.backend.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class OukilsonConfiguration {


    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
