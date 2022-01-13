package fr.oukilson.backend.configurations;

import fr.oukilson.backend.repository.GameRepository;
import fr.oukilson.backend.services.GameService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfiguration {

    @Bean
    public GameService gameService(GameRepository repository, ModelMapper mapper) {
        return new GameService(repository, mapper);
    }
}
