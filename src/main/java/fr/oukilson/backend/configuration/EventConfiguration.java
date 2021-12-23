package fr.oukilson.backend.configuration;

import fr.oukilson.backend.repository.EventRepository;
import fr.oukilson.backend.service.EventService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {
    @Bean
    public EventService eventService(EventRepository repository, ModelMapper mapper) {
        return new EventService(repository, mapper);
    }
}
