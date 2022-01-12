package fr.oukilson.backend.services;


import fr.oukilson.backend.dtos.GameDTO;
import fr.oukilson.backend.dtos.GameUuidDTO;
import fr.oukilson.backend.entities.Game;
import fr.oukilson.backend.repository.GameRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.util.*;

public class GameService {


    private GameRepository repository;
    private ModelMapper mapper;

    public GameService(GameRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Provides a game in function of its uuid
     * @param uuid
     * @return
     */
    public Optional<GameUuidDTO> findByUuid(String uuid) throws NoSuchElementException {
        Optional<Game> game = this.repository.findByUuid(uuid);
        return Optional.of(mapper.map(game.get(), GameUuidDTO.class));
    }

    /**
     * Provides a list of game in function of its name or a name part
     * @param name
     * @return Optional<GameUuidDTO>
     */
    public List<GameUuidDTO> findByName(String name) {
        List<GameUuidDTO> result = new LinkedList<>();
        repository.findAllByNameContaining(name).forEach(
                g -> result.add(this.mapper.map(g, GameUuidDTO.class))
        );
        return result;
    }

    public Optional<GameDTO> displayByUuid(String uuid) throws NoSuchElementException {
        Optional<Game> game = this.repository.findByUuid(uuid);
        return Optional.of(mapper.map(game.get(), GameDTO.class));
    }



}
