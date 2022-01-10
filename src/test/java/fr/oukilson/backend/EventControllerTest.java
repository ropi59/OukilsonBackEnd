package fr.oukilson.backend;

import com.google.gson.*;
import fr.oukilson.backend.controller.EventController;
import fr.oukilson.backend.dto.event.*;
import fr.oukilson.backend.entity.Event;
import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.entity.Location;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.service.EventService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = EventController.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventService service;
    private final String route = "/events";

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

    /**
     * Create a valid User entity with all attributes set to valid data.
     * @param id User's id in database
     * @param nickname User's unique nickname
     * @return User
     */
    private User createValidFullUser(Long id, String nickname) {
        User user = new User();
        user.setId(id);
        user.setNickname(nickname);
        user.setPassword("d1e8a70b5ccab1dc2f56bbf7e99f064a660c08e361a35751b9c483c88943d082");
        user.setEmail("email@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }

    /**
     * Create a valid Event entity with all attributes set to valid data.
     * @param id Event's id in database
     * @param game Game for the event; must use method createValidFullGame
     * @param user User who created the event; must use method createValidFullUser
     * @return Event
     */
    private Event createValidEvent(Long id, Game game, User user, Location location) {
        Event event = new Event();
        event.setId(id);
        event.setUuid(UUID.randomUUID().toString());
        event.setTitle("Valid event's title.");
        event.setMinPlayer(2);
        event.setMaxPlayer(5);
        event.setIsPrivate(false);
        event.setDescription("Une description plus que valide. YEAAAAAHHHHHHHHHHHHHH !!!!!!!!!!!!");
        event.setLocation(location);
        event.setGame(game);
        LocalDateTime localDateTime = LocalDateTime.now();
        event.setCreationDate(localDateTime);
        event.setLimitDate(localDateTime.plusDays(1L));
        event.setStartingDate(localDateTime.plusDays(2L));
        event.setEndingDate(event.getStartingDate().plusHours(5L));
        event.setCreator(user);
        return event;
    }

    /**
     * Return a Gson's instance which can deserialize and serialize LocalDateTime
     * @return Gson
     */
    private Gson getInitializedGSON() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, context)
                                -> LocalDateTime.parse(json.getAsJsonPrimitive().getAsString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (date, type, context)
                                -> new JsonPrimitive(date.toString()))
                .create();
    }

    // Test findByUuid route

    /**
     * Test the method findByUuid when given an uuid which doesn't exist in the database
     */
    @DisplayName("Test : find a user who doesn't exist in database.")
    @Test
    public void testFindByUuidWhenEventDoesntExist() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/12345"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test the method findByUuid when given a valid uuid
     */
    @DisplayName("Test : find a user who exists in database.")
    @Test
    public void testFindByUuid() throws Exception {
        // Mocking
        User user = this.createValidFullUser(3L, "toto");
        Game game = this.createValidFullGame(23L, "7 Wonders");
        Location location = new Location(620L, "Euralille", "59777", "1 Place François Mitterrand");
        Event event = this.createValidEvent(465L, game, user, location);
        ModelMapper mapper = new ModelMapper();
        EventDTO eventDTO = mapper.map(event, EventDTO.class);
        Mockito.when(service.findByUuid(eventDTO.getUuid())).thenReturn(eventDTO);

        // Send request
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+eventDTO.getUuid()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Assert
        Gson gson = this.getInitializedGSON();
        EventDTO resultDTO = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO.class);
        Assertions.assertEquals(eventDTO, resultDTO);
    }

    // Test FindAllByFilters route

    /**
     * Test when all attributes of EventSearchDTO are null
     */
    @DisplayName("Test : find all events by filters but all filters are null")
    @Test
    public void testFindAllByFiltersWhenNoParamGiven() throws Exception {
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get(route+"/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(new EventSearchDTO())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    /**
     * Test when the attribute startingDate of EventSearchDTO is null.
     * EventSearchDTO.town will be used for the search.
     */
    @DisplayName("Test : find all events by filters but the date filter is null")
    @Test
    public void testFindAllByFiltersWhenStartingDateIsNull() throws Exception {
        // Mocking
        LocalDateTime date = LocalDateTime.now();
        ModelMapper mapper = new ModelMapper();
        User user = this.createValidFullUser(1L, "tata");
        Game game = this.createValidFullGame(1L, "The game");
        Location location1 = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées");
        EventDTO eventDTO1 = mapper.map(this.createValidEvent(1L, game, user, location1), EventDTO.class);
        Location location2 = new Location(45L, "Paris", "75008", "Avenue des Champs Elysée");
        EventDTO eventDTO2 = mapper.map(this.createValidEvent(1L, game, user, location2), EventDTO.class);
        EventSearchDTO searchDTO1 = new EventSearchDTO(date.minusYears(1L), "Paris");
        Mockito.when(service.findByFilter(searchDTO1)).thenReturn(List.of(eventDTO1));
        EventSearchDTO searchDTO2 = new EventSearchDTO(null, "Paris");
        Mockito.when(service.findByFilter(searchDTO2)).thenReturn(List.of(eventDTO2));
        EventSearchDTO searchDTO3 = new EventSearchDTO(date.minusYears(1L), null);
        Mockito.when(service.findByFilter(searchDTO3)).thenReturn(List.of(eventDTO1));

        // Send request
        Gson gson = this.getInitializedGSON();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(route + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(searchDTO2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn();

        // Assert
        EventDTO[] array = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO[].class);
        Assertions.assertEquals(1, array.length);
        Assertions.assertEquals(eventDTO2, array[0]);
    }

    /**
     * Test when the attribute town of EventSearchDTO is null.
     * EventSearchDTO.startingDate will be used for the search.
     */
    @DisplayName("Test : find all events by filters but the town filter is null")
    @Test
    public void testFindAllByFiltersWhenTownIsNull() throws Exception {
        // Mocking
        LocalDateTime date = LocalDateTime.now();
        ModelMapper mapper = new ModelMapper();
        User user = this.createValidFullUser(1L, "toto");
        Game game = this.createValidFullGame(1L, "The game");
        Location location1 = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées");
        EventDTO eventDTO1 = mapper.map(this.createValidEvent(1L, game, user, location1), EventDTO.class);
        Location location2 = new Location(45L, "Paris", "75008", "Avenue des Champs Elysée");
        EventDTO eventDTO2 = mapper.map(this.createValidEvent(1L, game, user, location2), EventDTO.class);
        EventSearchDTO searchDTO1 = new EventSearchDTO(date.minusYears(1L), "Paris");
        Mockito.when(service.findByFilter(searchDTO1)).thenReturn(List.of(eventDTO1));
        EventSearchDTO searchDTO2 = new EventSearchDTO(null, "Paris");
        Mockito.when(service.findByFilter(searchDTO2)).thenReturn(List.of(eventDTO2));
        EventSearchDTO searchDTO3 = new EventSearchDTO(date.minusYears(1L), null);
        Mockito.when(service.findByFilter(searchDTO3)).thenReturn(List.of(eventDTO1));

        // Send request
        Gson gson = this.getInitializedGSON();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(route + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(searchDTO3)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn();

        // Assert
        EventDTO[] array = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO[].class);
        Assertions.assertEquals(1, array.length);
        Assertions.assertEquals(eventDTO1, array[0]);
    }

    /**
     * Test when all attributes of EventSearchDTO are initialized.
     * EventSearchDTO.startingDate will be used for the search.
     */
    @DisplayName("Test : find all events by filters with all filters initialized")
    @Test
    public void testFindAllByFilters() throws Exception {
        // Mocking
        LocalDateTime date = LocalDateTime.now();
        ModelMapper mapper = new ModelMapper();
        User user = this.createValidFullUser(1L, "toto");
        Game game = this.createValidFullGame(1L, "The game");
        Location location1 = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées");
        EventDTO eventDTO1 = mapper.map(this.createValidEvent(1L, game, user, location1), EventDTO.class);
        Location location2 = new Location(45L, "Paris", "75008", "Avenue des Champs Elysée");
        EventDTO eventDTO2 = mapper.map(this.createValidEvent(1L, game, user, location2), EventDTO.class);
        EventSearchDTO searchDTO1 = new EventSearchDTO(date.minusYears(1L), "Paris");
        Mockito.when(service.findByFilter(searchDTO1)).thenReturn(List.of(eventDTO1));
        EventSearchDTO searchDTO2 = new EventSearchDTO(null, "Paris");
        Mockito.when(service.findByFilter(searchDTO2)).thenReturn(List.of(eventDTO2));
        EventSearchDTO searchDTO3 = new EventSearchDTO(date.minusYears(1L), null);
        Mockito.when(service.findByFilter(searchDTO3)).thenReturn(List.of(eventDTO1));

        // Send request
        Gson gson = this.getInitializedGSON();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(route + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(searchDTO1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn();

        // Assert
        EventDTO[] array = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO[].class);
        Assertions.assertEquals(1, array.length);
        Assertions.assertEquals(eventDTO1, array[0]);
    }

    // Test delete route

    /**
     * Test when deleting an existing event
     */
    @DisplayName("Test : delete an existing event.")
    @Test
    public void testDeleteByUuid() throws Exception {
        EventDeleteDTO eventDTO = new EventDeleteDTO(UUID.randomUUID().toString());
        this.mockMvc.perform(MockMvcRequestBuilders.delete(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(eventDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }

    /**
     * Test when deleting a non-existing event
     */
    @DisplayName("Test : delete a non-existing event")
    @Test
    public void testDeleteByUuidWhenUuidNotValid() throws Exception {
        EventDeleteDTO eventDTO = new EventDeleteDTO("0");
        this.mockMvc.perform(MockMvcRequestBuilders.delete(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(eventDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }

    /**
     * Test when deleting null uuid
     */
    @DisplayName("Test : delete an event when the provided uuid is null")
    @Test
    public void testDeleteByUuidWhenUuidIsNull() throws Exception {
        EventDeleteDTO eventDTO = new EventDeleteDTO();
        this.mockMvc.perform(MockMvcRequestBuilders.delete(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(eventDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }

    // Test save route

    /**
     * Test event creation when everything is ok
     */
    @DisplayName("Test : create an event with valid fields")
    @Test
    public void testSave() throws Exception {
        // Mocking
        ModelMapper mapper = new ModelMapper();
        User user = this.createValidFullUser(1L, "toto");
        Game game = this.createValidFullGame(1L, "The game");
        Location location = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées");
        Event event = this.createValidEvent(1L, game, user, location);
        EventDTO eventDTO = mapper.map(event, EventDTO.class);
        EventCreateDTO eventCreateDTO = mapper.map(event, EventCreateDTO.class);
        Mockito.when(service.save(ArgumentMatchers.any(EventCreateDTO.class))).thenReturn(eventDTO);

        // Send request
        Gson gson = this.getInitializedGSON();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(eventCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // Assert
        EventDTO response = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO.class);
        Assertions.assertEquals(eventDTO, response);
    }

    /**
     * Test event creation when there's one or several invalid fields
     */
    @DisplayName("Test : create event with invalid body")
    @Test
    public void testSaveWhenEventCreateDTOIsInvalid() throws Exception {
        // Create but don't mock anything, must expect 400
        ModelMapper mapper = new ModelMapper();
        User user = this.createValidFullUser(1L, "toto");
        Game game = this.createValidFullGame(1L, "The game");
        Location location = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées");
        Event event = this.createValidEvent(1L, game, user, location);
        EventCreateDTO eventCreateDTO = mapper.map(event, EventCreateDTO.class);
        eventCreateDTO.setStartingDate(null);

        // Request
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(eventCreateDTO))).
                andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Testing update route

    /**
     * Test event update when everything is ok
     */
    @DisplayName("Test : update an event with valid fields")
    @Test
    public void testUpdate() throws Exception {
        // Mocking
        ModelMapper mapper = new ModelMapper();
        User user = this.createValidFullUser(1L, "toto");
        Game game = this.createValidFullGame(1L, "The game");
        Location location = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées");
        Event event = this.createValidEvent(1L, game, user, location);
        EventDTO oldEvent = mapper.map(event, EventDTO.class);
        event.setMinPlayer(4);
        EventDTO newEvent = mapper.map(event, EventDTO.class);
        EventUpdateDTO toUpdate = mapper.map(event, EventUpdateDTO.class);
        Mockito.when(service.update(ArgumentMatchers.any(EventUpdateDTO.class))).thenReturn(newEvent);

        // Send request
        Gson gson = this.getInitializedGSON();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(toUpdate)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // Assert
        EventDTO response = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO.class);
        Assertions.assertEquals(newEvent, response);
        Assertions.assertNotEquals(oldEvent, response);
    }

    /**
     * Test event update when there's one or several invalid fields
     */
    @DisplayName("Test : update event with invalid body")
    @Test
    public void testUpdateWhenEventUpdateDTOIsInvalid() throws Exception {
        // Create but don't mock anything, must expect 400
        ModelMapper mapper = new ModelMapper();
        User user = this.createValidFullUser(1L, "toto");
        Game game = this.createValidFullGame(1L, "The game");
        Location location = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées");
        Event event = this.createValidEvent(1L, game, user, location);
        EventUpdateDTO toUpdate = mapper.map(event, EventUpdateDTO.class);

        // Request
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(toUpdate))).
                andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}