package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.GameDTO;
import fr.oukilson.backend.dto.GameUuidDTO;
import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.repository.GameRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameServiceTest {
    @MockBean
    private GameRepository repository;
    @Autowired
    private ModelMapper mapper;
    private GameService service;

    @BeforeAll
    public void init() {
        this.service = new GameService(repository, mapper);
    }

    // Convenient methods for testing

    /**
     * Return a valid Game entity with all attributes set to valid data.
     * @param id Game's id in database
     * @param name Game's name
     * @return Game
     */
    private Game createValidFullGame(Long id, String name) {
        Game game = new Game();
        game.setId(id);
        game.setName(name);
        game.setUuid(UUID.randomUUID().toString());
        game.setMinPlayer(2);
        game.setMaxPlayer(5);
        game.setMinAge(6);
        game.setCreatorName("Le créateur d'un jeu");
        game.setMinPlayingTime(30);
        game.setMaxPlayingTime(120);
        return game;
    }

    // Method findByUuid

    /**
     * Test findByUuid with null parameter
     */
    @Test
    @DisplayName("Test findByUuid : null parameter")
    public void testFindByUuidNullParameter() {
        Assertions.assertNull(this.service.findByUuid(null));
    }

    /**
     * Test findByUuid with game presents in database
     */
    @DisplayName("Test findByUuid : game is in database")
    @Test
    public void testFindByUuidGameInDatabase() {
        Game game = this.createValidFullGame(1L, "Root");
        BDDMockito.when(this.repository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        GameDTO dto = this.service.findByUuid(game.getUuid());
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(this.mapper.map(game, GameDTO.class), dto);
    }

    /**
     * Test findByUuid with game not in database
     */
    @DisplayName("Test findByUuid : game is not in database")
    @Test
    public void testFindByUuidGameNotInDatabase() {
        Game game = this.createValidFullGame(1L, "Root");
        GameDTO dto = this.service.findByUuid(game.getUuid());
        Assertions.assertNull(dto);
    }

    // Method findByName

    /**
     * Test findByName with null parameter
     */
    @Test
    @DisplayName("Test findByName : null parameter")
    public void testFindByNameNullParameter() {
        List<GameUuidDTO> list = this.service.findByName(null);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.size());
    }

    /**
     * Test findByName with the look-up string is null
     */
    @Test
    @DisplayName("Test findByName : null given name")
    public void testFindByNameNullName() {
        GameUuidDTO dto = new GameUuidDTO();
        List<GameUuidDTO> list = this.service.findByName(dto);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.size());
    }

    /**
     * Test findByName with no result found
     */
    @Test
    @DisplayName("Test findByName : no result found")
    public void testFindByNameWithNoResultFound() {
        GameUuidDTO dto = new GameUuidDTO(null, "7 Wonders");
        BDDMockito.when(this.repository.findAllByNameContaining(dto.getName())).thenReturn(new LinkedList<>());
        List<GameUuidDTO> list = this.service.findByName(dto);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.size());
    }

    /**
     * Test findByName with some result found
     */
    @Test
    @DisplayName("Test findByName : result found")
    public void testFindByNameWithResultFound() {
        GameUuidDTO dto = new GameUuidDTO(null, "7 Wonders");
        List<Game> games = new LinkedList<>();
        int size = 4;
        for (int i=0; i<size; i++) {
            games.add(this.createValidFullGame((long) i, "Jeux n°"+i));
        }
        BDDMockito.when(this.repository.findAllByNameContaining(dto.getName())).thenReturn(games);
        List<GameUuidDTO> list = this.service.findByName(dto);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(size, list.size());
        for (int i=0; i<size; i++) {
            Assertions.assertEquals(this.mapper.map(games.get(i), GameUuidDTO.class), list.get(i));
        }
    }
}
