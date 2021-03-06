package fr.oukilson.backend.controller;

import com.google.gson.*;
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
import java.util.LinkedList;
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
        event.setPrivate(false);
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
        Location location = new Location(620L, "Euralille", "59777", "1 Place François Mitterrand", null);
        Event event = this.createValidEvent(465L, game, user, location);
        location.setEvent(event);
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
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/search"))
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
        String town = "Paris";
        int size = 2;
        Game game = this.createValidFullGame(1L, "The game");
        User user = this.createValidFullUser(1L, "tata");
        List<EventDTO> events = new LinkedList<>();
        ModelMapper mapper = new ModelMapper();
        for (int i=0; i<size; i++) {
            Location location =
                new Location(45L, "Paris", "75008", "Avenue des Champs Elysée", null);
            Event event = this.createValidEvent((long)i, game, user, location);
            event.setLocation(location);
            location.setEvent(event);
            events.add(mapper.map(event, EventDTO.class));
        }
        Mockito.when(this.service.findByFilter("", town)).thenReturn(events);

        // Send Request
        Gson gson = this.getInitializedGSON();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(route + "/search?date=&town="+town))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn();

        // Assert
        EventDTO[] array = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO[].class);
        Assertions.assertEquals(size, array.length);
        for (int i=0; i<size; i++) {
            Assertions.assertEquals(events.get(i), array[i]);
        }
    }

    /**
     * Test when the attribute town of EventSearchDTO is null.
     * EventSearchDTO.startingDate will be used for the search.
     */
    @DisplayName("Test : find all events by filters but the town filter is null")
    @Test
    public void testFindAllByFiltersWhenTownIsNull() throws Exception {
        // Setting up
        LocalDateTime mytime = LocalDateTime.now().minusMonths(2);
        List<EventDTO> events = new LinkedList<>();
        ModelMapper mapper = new ModelMapper();
        int size = 5;
        for (int i=0; i<size; i++) {
            Location location =
                    new Location((long)i, "Paris "+i, "75008", "Avenue des Champs Elysée", null);
            Game game = this.createValidFullGame((long)i, "Un jeu random "+i);
            User user = this.createValidFullUser((long)i, "un user "+i);
            Event event = this.createValidEvent((long)i, game, user, location);
            location.setEvent(event);
            event.setStartingDate(mytime.plusMonths(8));
            events.add(mapper.map(event, EventDTO.class));
        }
        Mockito.when(this.service.findByFilter(mytime.toString(), "")).thenReturn(events);

        // Request
        Gson gson = this.getInitializedGSON();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(route + "/search?date="+ mytime +"&town="))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn();

        // Assertions
        EventDTO[] array = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO[].class);
        Assertions.assertEquals(size, array.length);
        for (int i=0; i<size; i++) {
            Assertions.assertEquals(events.get(i), array[i]);
        }
    }

    /**
     * Test when all attributes of EventSearchDTO are initialized.
     * EventSearchDTO.startingDate will be used for the search.
     */
    @DisplayName("Test : find all events by filters with all filters initialized")
    @Test
    public void testFindAllByFilters() throws Exception {
        // Setting up
        ModelMapper mapper = new ModelMapper();
        List<EventDTO> townEvents = new LinkedList<>();
        int townEventsSize = 3;
        List<EventDTO> dateEvents = new LinkedList<>();
        int dateEventsSize = townEventsSize+1;
        Game game = this.createValidFullGame(1L, "The game");
        User user = this.createValidFullUser(1L, "tata");
        String town = "Pau";
        for (int i=0; i<townEventsSize; i++) {
            Location location =
                new Location((long)i, town, "64000", "Boulevard des Pyrénées", null);
            Event event = this.createValidEvent((long)i, game, user, location);
            location.setEvent(event);
            townEvents.add(mapper.map(event, EventDTO.class));
        }
        for (int i=0; i<dateEventsSize; i++) {
            Location location = new Location(10L *i, "Ville "+i, null, null, null);
            Event event = this.createValidEvent(10L*i, game, user, location);
            location.setEvent(event);
            dateEvents.add(mapper.map(event, EventDTO.class));
        }
        String date = dateEvents.get(0).getStartingDate().minusDays(10).toString();
        Mockito.when(this.service.findByFilter(date, town)).thenReturn(dateEvents);
        Mockito.when(this.service.findByFilter(date, "")).thenReturn(dateEvents);
        Mockito.when(this.service.findByFilter("", town)).thenReturn(townEvents);

        // Request
        Gson gson = this.getInitializedGSON();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(route + "/search?date="+date+"&town="+town))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn();

        // Assertions
        EventDTO[] array = gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                EventDTO[].class);
        Assertions.assertEquals(dateEventsSize, array.length);
        for (int i=0; i<dateEventsSize; i++) {
            Assertions.assertEquals(dateEvents.get(i), array[i]);
        }
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
     * Test event creation with a null body
     */
    @DisplayName("Test : saving an event with a null body")
    @Test
    public void testSaveNullBody() throws Exception {
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test event creation when the creation method throws an exception
     */
    @DisplayName("Test : event creation when the save function throws an exception")
    @Test
    public void testSaveCatchBranch() throws Exception {
        Mockito.when(this.service.save(ArgumentMatchers.any(EventCreateDTO.class))).thenThrow(new RuntimeException());
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

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
        Location location = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées", null);
        Event event = this.createValidEvent(1L, game, user, location);
        location.setEvent(event);
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
        Location location = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées", null);
        Event event = this.createValidEvent(1L, game, user, location);
        location.setEvent(event);
        EventCreateDTO eventCreateDTO = mapper.map(event, EventCreateDTO.class);
        eventCreateDTO.setStartingDate(null);

        // Request
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(eventCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Testing update route

    /**
     * Test event update with null body
     */
    @DisplayName("Test : updating an event with a null body")
    @Test
    public void testUpdateNullBody() throws Exception {
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test event update when the update method throws an exception
     */
    @DisplayName("Test : event update when the update function throws an exception")
    @Test
    public void testUpdateCatchBranch() throws Exception {
        Mockito.when(this.service.update(ArgumentMatchers.any(EventUpdateDTO.class))).thenThrow(new RuntimeException());
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

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
        Location location = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées", null);
        Event event = this.createValidEvent(1L, game, user, location);
        location.setEvent(event);
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
        Location location = new Location(45L, "Pau", "64000", "Boulevard des Pyrénées", null);
        Event event = this.createValidEvent(1L, game, user, location);
        location.setEvent(event);
        EventUpdateDTO toUpdate = mapper.map(event, EventUpdateDTO.class);

        // Request
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(toUpdate)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Method addUserInEvent

    /**
     * Test addUserInEvent with a null body
     */
    @DisplayName("Test addUserInEvent : null body")
    @Test
    public void testAddUserInEventWithNullValue() throws Exception {
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test addUserInEvent with a null user's name
     */
    @DisplayName("Test addUserInEvent : null user's name")
    @Test
    public void testAddUserInEventWithNullUserName() throws Exception {
        EventAddUserDTO body = new EventAddUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", null);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    /**
     * Test addUserInEvent with a null event's uuid
     */
    @DisplayName("Test addUserInEvent : null event's uuid")
    @Test
    public void testAddUserInEventWithNullEventUuid() throws Exception {
        EventAddUserDTO body = new EventAddUserDTO(null, "Toto");
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    /**
     * Test addUserInEvent when everything is ok
     */
    @DisplayName("Test addUserInEvent : everything is ok")
    @Test
    public void testAddUserInEvent() throws Exception {
        EventAddUserDTO body = new EventAddUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", "Toto");
        Mockito.when(this.service.addUserInEvent(ArgumentMatchers.any(EventAddUserDTO.class))).thenReturn(true);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(true));
    }

    /**
     * Test addUserInEvent when game or user is not found
     */
    @DisplayName("Test addUserInEvent : game or user not found")
    @Test
    public void testAddUserInEventUnknownElementInDatabase() throws Exception {
        EventAddUserDTO body = new EventAddUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", "Toto");
        Mockito.when(this.service.addUserInEvent(ArgumentMatchers.any(EventAddUserDTO.class))).thenReturn(false);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    // Method addUserInEventInWaitingQueue

    /**
     * Test addUserInEventInWaitingQueue with a null body
     */
    @DisplayName("Test addUserInEventInWaitingQueue : null body")
    @Test
    public void testAddUserInEventInWaitingQueueWithNullValue() throws Exception {
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test addUserInEventInWaitingQueue with a null user's name
     */
    @DisplayName("Test addUserInEventInWaitingQueue : null user's name")
    @Test
    public void testAddUserInEventInWaitingQueueWithNullUserName() throws Exception {
        EventAddUserDTO body = new EventAddUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", null);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    /**
     * Test addUserInEventInWaitingQueue with a null event's uuid
     */
    @DisplayName("Test addUserInEventInWaitingQueue : null event's uuid")
    @Test
    public void testAddUserInEventInWaitingQueueWithNullEventUuid() throws Exception {
        EventAddUserDTO body = new EventAddUserDTO(null, "Toto");
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    /**
     * Test addUserInEventInWaitingQueue when everything is ok
     */
    @DisplayName("Test addUserInEventInWaitingQueue : everything is ok")
    @Test
    public void testAddUserInEventInWaitingQueue() throws Exception {
        EventAddUserDTO body = new EventAddUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", "Toto");
        Mockito.when(this.service.addUserInEventInWaitingQueue(ArgumentMatchers.any(EventAddUserDTO.class))).thenReturn(true);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(true));
    }

    /**
     * Test addUserInEventInWaitingQueue when game or user is not found
     */
    @DisplayName("Test addUserInEventInWaitingQueue : game or user not found")
    @Test
    public void testAddUserInEventInWaitingQueueUnknownElementInDatabase() throws Exception {
        EventAddUserDTO body = new EventAddUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", "Toto");
        Mockito.when(this.service.addUserInEventInWaitingQueue(ArgumentMatchers.any(EventAddUserDTO.class))).thenReturn(false);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/add_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    // Method removeUserInEvent

    /**
     * Test removeUserInEvent with a null body
     */
    @DisplayName("Test removeUserInEvent : null body")
    @Test
    public void testRemoveUserInEventWithNullValue() throws Exception {
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test removeUserInEvent with a null user's name
     */
    @DisplayName("Test removeUserInEvent : null user's name")
    @Test
    public void testRemoveUserInEventWithNullUserName() throws Exception {
        EventRemoveUserDTO body = new EventRemoveUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", null);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    /**
     * Test removeUserInEvent with a null event's uuid
     */
    @DisplayName("Test removeUserInEvent : null event's uuid")
    @Test
    public void testRemoveUserInEventWithNullEventUuid() throws Exception {
        EventRemoveUserDTO body = new EventRemoveUserDTO(null, "Toto");
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    /**
     * Test removeUserInEvent when everything is ok
     */
    @DisplayName("Test removeUserInEvent : everything is ok")
    @Test
    public void testRemoveUserInEvent() throws Exception {
        EventRemoveUserDTO body = new EventRemoveUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", "Toto");
        Mockito.when(this.service.removeUserInEvent(ArgumentMatchers.any(EventRemoveUserDTO.class))).thenReturn(true);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(true));
    }

    /**
     * Test removeUserInEvent when game or user is not found
     */
    @DisplayName("Test removeUserInEvent : game or user not found")
    @Test
    public void testRemoveUserInEventUnknownElementInDatabase() throws Exception {
        EventRemoveUserDTO body = new EventRemoveUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", "Toto");
        Mockito.when(this.service.removeUserInEvent(ArgumentMatchers.any(EventRemoveUserDTO.class))).thenReturn(false);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    // Method removeUserInWaitingQueue

    /**
     * Test removeUserInWaitingQueue with a null body
     */
    @DisplayName("Test removeUserInWaitingQueue : null body")
    @Test
    public void tesRemoveUserInWaitingQueueWithNullValue() throws Exception {
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test removeUserInWaitingQueue with a null user's name
     */
    @DisplayName("Test removeUserInWaitingQueue : null user's name")
    @Test
    public void testRemoveUserInWaitingQueueWithNullUserName() throws Exception {
        EventRemoveUserDTO body = new EventRemoveUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", null);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    /**
     * Test removeUserInWaitingQueue with a null event's uuid
     */
    @DisplayName("Test removeUserInWaitingQueue : null event's uuid")
    @Test
    public void testRemoveUserInWaitingQueueWithNullEventUuid() throws Exception {
        EventRemoveUserDTO body = new EventRemoveUserDTO(null, "Toto");
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }

    /**
     * Test removeUserInWaitingQueue when everything is ok
     */
    @DisplayName("Test removeUserInWaitingQueue : everything is ok")
    @Test
    public void testRemoveUserInWaitingQueue() throws Exception {
        EventRemoveUserDTO body = new EventRemoveUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", "Toto");
        Mockito
                .when(this.service.removeUserInWaitingQueue(ArgumentMatchers.any(EventRemoveUserDTO.class)))
                .thenReturn(true);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(true));
    }

    /**
     * Test removeUserInWaitingQueue when game or user is not found
     */
    @DisplayName("Test removeUserInWaitingQueue : game or user not found")
    @Test
    public void testRemoveUserInWaitingQueueUnknownElementInDatabase() throws Exception {
        EventRemoveUserDTO body = new EventRemoveUserDTO("50b3e71f-cd84-4898-87ea-69d33c4bd7d5", "Toto");
        Mockito
                .when(this.service.removeUserInWaitingQueue(ArgumentMatchers.any(EventRemoveUserDTO.class)))
                .thenReturn(false);
        Gson gson = this.getInitializedGSON();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(route+"/remove_user/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(false));
    }
}