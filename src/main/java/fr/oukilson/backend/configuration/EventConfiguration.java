package fr.oukilson.backend.configuration;

import fr.oukilson.backend.repository.EventRepository;
import fr.oukilson.backend.repository.GameRepository;
import fr.oukilson.backend.repository.LocationRepository;
import fr.oukilson.backend.repository.UserRepository;
import fr.oukilson.backend.service.EventService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {
    @Bean
    public EventService eventService(EventRepository eventRepo,
                                     UserRepository userRepo,
                                     GameRepository gameRepo,
                                     LocationRepository locationRepo,
                                     ModelMapper mapper) {
        return new EventService(eventRepo, userRepo, gameRepo, locationRepo, mapper);
    }
}
