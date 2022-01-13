package fr.oukilson.backend.services;

import fr.oukilson.backend.dtos.GameDTO;
import fr.oukilson.backend.dtos.GameUuidDTO;
import fr.oukilson.backend.entities.Game;
import fr.oukilson.backend.repository.GameRepository;
import org.modelmapper.ModelMapper;
import java.util.*;

public class GameService {
    private GameRepository repository;
    private ModelMapper mapper;

    public GameService(GameRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Return all the game info by providing its uuid.
     * @param uuid String uuid of the game
     * @return Optional<GameUuidDTO>
     */
    public GameDTO findByUuid(String uuid) {
        GameDTO result = null;
        Optional<Game> game = this.repository.findByUuid(uuid);
        if (game.isPresent()) result = this.mapper.map(game.get(), GameDTO.class);
        return result;
    }

    /**
     * Return a list of all games sharing the same name or part of it.
     * @param game GameUuidDTO
     * @return List of GameUuidDTO
     */
    public List<GameUuidDTO> findByName(GameUuidDTO game) {
        List<GameUuidDTO> result = new LinkedList<>();
        repository.findAllByNameContaining(game.getName()).forEach(
                g -> result.add(this.mapper.map(g, GameUuidDTO.class))
        );
        return result;
    }
}
