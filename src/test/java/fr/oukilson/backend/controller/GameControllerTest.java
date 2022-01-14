package fr.oukilson.backend.controller;

import com.google.gson.Gson;
import fr.oukilson.backend.dto.GameDTO;
import fr.oukilson.backend.dto.GameUuidDTO;
import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = GameController.class)
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService service;
    private final String route = "/games";

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

    // Route findByUuid GET

    /**
     * Test findByUuid when the given uuid corresponds to an actual game
     */
    @DisplayName("Test findByUuid : uuid corresponding to an actual game")
    @Test
    public void testFindByUuidWhenGameFound() throws Exception {
        // Mocking
        Game game = this.createValidFullGame(1L, "Lords of Waterdeep");
        ModelMapper mapper = new ModelMapper();
        GameDTO dto = mapper.map(game, GameDTO.class);
        Mockito.when(this.service.findByUuid(game.getUuid())).thenReturn(dto);

        // Send request
        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get(route+"/"+game.getUuid()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Assert
        Gson gson = new Gson();
        GameDTO resultDTO = gson.fromJson(
                result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                GameDTO.class);
        Assertions.assertNotNull(resultDTO);
        Assertions.assertEquals(dto, resultDTO);
    }

    /**
     * Test findByUuid when the given uuid do not correspond to an actual game
     */
    @DisplayName("Test findByUuid : uuid not corresponding to an actual game")
    @Test
    public void testFindByUuidWhenGameNotFound() throws Exception {
        // Mocking
        Game game = this.createValidFullGame(1L, "Lords of Waterdeep");
        Mockito.when(this.service.findByUuid(game.getUuid())).thenReturn(null);

        // Send request
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+game.getUuid()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // Route findByName POST

    /**
     * Test findByName with the search gives results
     */
    @DisplayName("Test findByName : result found")
    @Test
    public void testFindByNameReturnResults() throws Exception {
        // Mocking
        GameUuidDTO data = new GameUuidDTO("", "Jeux");
        List<GameUuidDTO> games = new LinkedList<>();
        ModelMapper mapper = new ModelMapper();
        int size = 3;
        for (int i=0; i<size; i++) {
            games.add(mapper.map(this.createValidFullGame((long) i, "Jeux n°"+i), GameUuidDTO.class));
        }
        BDDMockito.when(this.service.findByName(data)).thenReturn(games);

        // Send request
        Gson gson = new Gson();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(data)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn();

        // Assert
        GameUuidDTO[] resultDTO = gson.fromJson(
                result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                GameUuidDTO[].class);
        Assertions.assertNotNull(resultDTO);
        Assertions.assertEquals(size, resultDTO.length);
        for (int i=0; i<size; i++) {
            Assertions.assertEquals(games.get(i), resultDTO[i]);
        }
    }

    /**
     * Test findByName with the search gives no result
     */
    @DisplayName("Test findByName : no result found")
    @Test
    public void testFindByNameNoResultFound() throws Exception {
        GameUuidDTO data = new GameUuidDTO("", "o");
        BDDMockito.when(this.service.findByName(data)).thenReturn(new LinkedList<>());
        Gson gson = new Gson();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(data)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    /**
     * Test findByName with an empty search string
     */
    @DisplayName("Test findByName : empty search string")
    @Test
    public void testFindByNameWithEmptySearchString() throws Exception {
        GameUuidDTO data = new GameUuidDTO("", "");
        Gson gson = new Gson();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(data)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }
}
