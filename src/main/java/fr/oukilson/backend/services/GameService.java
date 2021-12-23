package fr.oukilson.backend.services;


import fr.oukilson.backend.dtos.GameDTO;
import fr.oukilson.backend.entities.Game;
import fr.oukilson.backend.repository.GameRepository;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GameService {


    private GameRepository repository;
    private ModelMapper mapper;

    public GameService(GameRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    /**
     * Return a list of games
     * @return List<gameDTO>
     */

    /**
    public List<gameDTO> findAll() {
        List<gameDTO> gameDTOList = new ArrayList<>();
        this.repository.findAll().forEach(employe -> {
            gameDTOList.add(mapper.map(employe, gameDTO.class));
        });
        return gameDTOList;
    }
*/


    /**
     * Return a game from its ID
     * @param id Long
     * @return Optional<GameDTO>
     */
    public Optional<GameDTO> findById(final int id) throws NoSuchElementException {
        Optional<Game> game = this.repository.findById(id);
        return Optional.of(mapper.map(game.get(), GameDTO.class));
    }






}
