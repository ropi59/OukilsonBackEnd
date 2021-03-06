package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.event.*;
import fr.oukilson.backend.entity.Event;
import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.entity.Location;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.EventRepository;
import fr.oukilson.backend.repository.GameRepository;
import fr.oukilson.backend.repository.LocationRepository;
import fr.oukilson.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class EventServiceTest {
    @MockBean
    private EventRepository repository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private LocationRepository locationRepository;
    @Autowired
    private ModelMapper mapper;
    private EventService service;

    @BeforeAll
    public void init() {
        service = new EventService(repository, userRepository, gameRepository, locationRepository, mapper);
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
        game.setCreatorName("Le cr??ateur d'un jeu");
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
        user.setFirstName(nickname);
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
        event.setTitle("Valid event's title. "+event.getUuid());
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

    // Method findByUuid

    /**
     * Testing when providing an unknown uuid for the event
     */
    @DisplayName("Test : find an event with an invalid UUID")
    @Test
    public void testFindByUuidWithWrongUuid() {
        Assertions.assertNull(this.service.findByUuid("00000000000000"));
    }

    /**
     * Testing when providing a correct uuid for the search
     */
    @DisplayName("Test : find an event in database by its uuid")
    @Test
    public void testFindByUuid() {
        // Mock event
        Game game = this.createValidFullGame(1L, "Inis");
        User user = this.createValidFullUser(1L, "toto");
        Location location = new Location(1L, "Euralille", "59777", "1 Place Fran??ois Mitterrand", null);
        Event event = this.createValidEvent(1L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));

        // Get the event
        EventDTO eventDTO = this.service.findByUuid(event.getUuid());

        // Assert
        Assertions.assertNotNull(eventDTO);
        EventDTO eventInDB = this.mapper.map(event, EventDTO.class);
        Assertions.assertEquals(eventInDB, eventDTO);
    }

    // Method findByFilter

    /**
     * Testing for search event method by giving a town filter.
     * Should return all events in the given town.
     */
    @DisplayName("Test : find all events, town only, no date")
    @Test
    public void testFindAllEventsByTownOnly() {
        // Setting up
        List<Event> events = new LinkedList<>();
        int size = 4;
        String town = "Lyon";
        for (int i=0; i<size; i++) {
            User user = this.createValidFullUser((long)i, "Nom"+i);
            Game game = this.createValidFullGame((long)i, "Jeu "+i);
            Location loc = new Location((long)i, town, null, null, null);
            Event event = this.createValidEvent((long)i, game, user, loc);
            loc.setEvent(event);
            events.add(event);
        }
        BDDMockito.when(this.repository.findAllByLocationTownContaining(town)).thenReturn(events);
        BDDMockito.when(this.repository.findAllByStartingDateAfter(ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(new LinkedList<>());

        List<EventDTO> result = this.service.findByFilter("", town);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(events.size(), result.size());
        for (int i=0; i<size; i++) {
            Assertions.assertEquals(this.mapper.map(events.get(i), EventDTO.class), result.get(i));
        }
    }

    /**
     * Testing for search event method by giving a date filter.
     * Must return all events after the given date.
     */
    @DisplayName("Test : find all events, date only, no town")
    @Test
    public void testFindAllEventsByDateAfterOnly() {
        // Setting up
        List<Event> events = new LinkedList<>();
        int size = 8;
        for (int i=0; i<size; i++) {
            User user = this.createValidFullUser((long)i, "Nom"+i);
            Game game = this.createValidFullGame((long)i, "Jeu "+i);
            Location loc = new Location((long)i, "Ville "+i, null, null, null);
            Event event = this.createValidEvent((long)i, game, user, loc);
            loc.setEvent(event);
            events.add(event);
        }
        LocalDateTime date = events.get(0).getStartingDate().minusYears(1);
        BDDMockito.when(this.repository.findAllByLocationTownContaining(ArgumentMatchers.anyString()))
                .thenReturn(new LinkedList<>());
        BDDMockito.when(this.repository.findAllByStartingDateAfter(date)).thenReturn(events);

        List<EventDTO> result = this.service.findByFilter(date.toString(), "");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(events.size(), result.size());
        for (int i=0; i<size; i++) {
            Assertions.assertEquals(this.mapper.map(events.get(i), EventDTO.class), result.get(i));
        }
    }

    /**
     * Testing for search event method by giving empty filters.
     * Must return an empty list.
     */
    @DisplayName("Test : find all events, empty date & town")
    @Test
    public void testFindAllEventsWithEmptyFilters() {
        EventSearchDTO toSearch = new EventSearchDTO();
        List<EventDTO> result = this.service.findByFilter("", "");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Testing for search event method by giving no filters.
     * Should return an empty list
     */
    @DisplayName("Test : search when date is null")
    @Test
    public void testFindAllEventsWithNullDate() {
        EventSearchDTO toSearch = new EventSearchDTO();
        List<EventDTO> result = this.service.findByFilter(null, null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Testing for search event method by giving null filters.
     * Should return an empty list
     */
    @DisplayName("Test : search when all filters are null")
    @Test
    public void testFindAllEventsWithNullDateAndNullTown() {
        EventSearchDTO toSearch = new EventSearchDTO();
        List<EventDTO> result = this.service.findByFilter(null, null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Testing for search event method by giving both town and date filters.
     * If both are present, then the date filter takes priority.
     */
    @DisplayName("Test : find events when town & date filters are initialized")
    @Test
    public void testFindAllEventsWithBothDateAndTownGiven() {
        String town = "Nancy";
        List<Event> townEvents = new LinkedList<>();
        List<Event> dateEvents = new LinkedList<>();
        int size = 3;
        for (int i=0; i<size; i++) {
            User user = this.createValidFullUser((long)i, "Nom"+i);
            Game game = this.createValidFullGame((long)i, "Jeu "+i);
            Location loc = new Location((long)i, "Ville "+i, null, null, null);
            Event event = this.createValidEvent((long)i, game, user, loc);
            loc.setEvent(event);
            dateEvents.add(event);
        }
        LocalDateTime date = dateEvents.get(0).getStartingDate().minusYears(1);
        size += 2;
        Location loc = new Location((long) size, town, null, null, null);
        for (int i=0; i<size; i++) {
            User user = this.createValidFullUser(2L *size+i, "NomBis"+i);
            Game game = this.createValidFullGame(2L *size+i, "JeuBis"+i);
            Event event = this.createValidEvent(2L *size+i, game, user, loc);
            loc.setEvent(event);
            townEvents.add(event);
        }

        BDDMockito.when(this.repository.findAllByLocationTownContaining(town)).thenReturn(townEvents);
        BDDMockito.when(this.repository.findAllByStartingDateAfter(date)).thenReturn(dateEvents);

        List<EventDTO> result = this.service.findByFilter(date.toString(), town);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(dateEvents.size(), result.size());
        Assertions.assertNotEquals(townEvents.size(), result.size());
        for (int i=0; i<dateEvents.size(); i++) {
            Assertions.assertEquals(this.mapper.map(dateEvents.get(i), EventDTO.class), result.get(i));
        }
    }

    // Method save

    /**
     * Testing correct event creation
     */
    @DisplayName("Test : create an event with valid data")
    @Test
    public void testSaveWhenAllDataAreValidAndPrivateIsFalse() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito.when(this.locationRepository.save(ArgumentMatchers.any(Location.class))).thenReturn(location);

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
            // The method service.save attributes its own uuid and creation date, so the previous handmade event
            // must alter before testing
            event.setUuid(result.getUuid());
            event.setCreationDate(result.getCreationDate());
            Assertions.assertEquals(this.mapper.map(event, EventDTO.class), result);
        }
    }

    /**
     * Testing correct event creation with no ending date
     */
    @DisplayName("Test : create an event with valid data and no ending date")
    @Test
    public void testSaveWhenAllDataAreValidAndEndingDateIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setEndingDate(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito.when(this.locationRepository.save(ArgumentMatchers.any(Location.class))).thenReturn(location);

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
            // The method service.save attributes its own uuid and creation date, so the previous handmade event
            // must alter before testing
            event.setUuid(result.getUuid());
            event.setCreationDate(result.getCreationDate());
            Assertions.assertEquals(this.mapper.map(event, EventDTO.class), result);
        }
    }

    /**
     * Testing if creating an event with no title throws IllegalArgumentException
     */
    @DisplayName("Test : event with no title throws IllegalArgumentException")
    @Test
    public void testSaveWhenEventTitleIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setTitle(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing if creating an event with minimal players less than 2 throws IllegalArgumentException
     */
    @DisplayName("Test : event with minimal players count less than 2 throws IllegalArgumentException")
    @Test
    public void testSaveWhenMinPlayerLessThan2() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setMinPlayer(1);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing correct event creation with minimal players count set to 2
     */
    @DisplayName("Test : create a valid event with minimal players count set to 2")
    @Test
    public void testSaveWhenMinPlayerEquals2() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setMinPlayer(2);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito.when(this.locationRepository.save(ArgumentMatchers.any(Location.class))).thenReturn(location);

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
            // The method service.save attributes its own uuid and creation date, so the previous handmade event
            // must alter before testing
            event.setUuid(result.getUuid());
            event.setCreationDate(result.getCreationDate());
            Assertions.assertEquals(this.mapper.map(event, EventDTO.class), result);
        }
    }

    /**
     * Testing correct event creation with minimal players count is equals to maximal players count
     */
    @DisplayName("Test : create a valid event with min player == max player")
    @Test
    public void testSaveWhenMinPlayerEqualsMaxPlayer() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setMinPlayer(3);
        event.setMaxPlayer(event.getMinPlayer());
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito.when(this.locationRepository.save(ArgumentMatchers.any(Location.class))).thenReturn(location);

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
            // The method service.save attributes its own uuid and creation date, so the previous handmade event
            // must alter before testing
            event.setUuid(result.getUuid());
            event.setCreationDate(result.getCreationDate());
            Assertions.assertEquals(this.mapper.map(event, EventDTO.class), result);
        }
    }

    /**
     * Testing if creating an event with minimal players less than maximal players count throws IllegalArgumentException
     */
    @DisplayName("Test : event with min players > max players throws IllegalArgumentException")
    @Test
    public void testSaveWhenMaxPlayerLessThanMinPlayer() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setMaxPlayer(event.getMinPlayer()-1);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing if creating an event with a null description throws IllegalArgumentException
     */
    @DisplayName("Test : event with a null description throws IllegalArgumentException")
    @Test
    public void testSaveWhenDescriptionIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setDescription(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing event creation with an invalid inscription date
     * Must throw IllegalArgumentException
     */
    @DisplayName("Test : event with invalid limit inscription date throws IllegalArgumentException")
    @Test
    public void testSaveWhenLimitDateIsBeforeCreationDate() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setLimitDate(event.getCreationDate().minusYears(1));
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }
    /**
     * Testing event creation with an invalid starting date
     * Must throw IllegalArgumentException
     */
    @DisplayName("Test : event with invalid starting date throws IllegalArgumentException")
    @Test
    public void testSaveWhenStartingDateIsBeforeLimitDate() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setStartingDate(event.getLimitDate().minusMonths(1));
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing event creation with an invalid ending date
     * Must throw IllegalArgumentException
     */
    @DisplayName("Test : event with invalid ending date throws IllegalArgumentException")
    @Test
    public void testSaveWhenEndingDateIsBeforeStartingDate() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setEndingDate(event.getStartingDate().minusDays(1));
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing event creation when creator attribute is null
     * Must throw IllegalArgumentException
     */
    @DisplayName("Test : event with null creator attribute IllegalArgumentException")
    @Test
    public void testSaveWhenCreatorIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setCreator(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Test event creation with a username not in database
     * Must throw NoSuchElementException
     */
    @DisplayName("Test : can't find creator user in database NoSuchElementException")
    @Test
    public void testSaveWhenCreatorIsNotInDatabase() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(NoSuchElementException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing event creation when game attribute is null
     * Must throw IllegalArgumentException
     */
    @DisplayName("Test : event with null game attribute IllegalArgumentException")
    @Test
    public void testSaveWhenGameIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setGame(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing creation event with a game not in database
     * Must throw NoSuchElementException
     */
    @DisplayName("Test : game for event not in database NoSuchElementException")
    @Test
    public void testSaveWhenGameIsNotInDatabase() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(NoSuchElementException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing event creation when location attribute is null
     * Must throw IllegalArgumentException
     */
    @DisplayName("Test : event with null location attribute IllegalArgumentException")
    @Test
    public void testSaveWhenLocationIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setLocation(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing event creation when location.town attribute is null
     * Must throw IllegalArgumentException
     */
    @DisplayName("Test : event with null town attribute IllegalArgumentException")
    @Test
    public void testSaveWhenLocationTownIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.getLocation().setTown(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    // Method update

    /**
     * Update event testing
     */
    @DisplayName("Test : update event with a new title")
    @Test
    public void testUpdateWhenAllDataAreValidAndPrivateIsFalse() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setTitle("A brand new title , Weather Hacker !!!");
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.update(toUpdate);
        }
        finally {
            Assertions.assertNotNull(result);
            Assertions.assertEquals(this.mapper.map(newEvent, EventDTO.class), result);
        }
    }

    /**
     * Update event testing when ending date is null
     */
    @DisplayName("Test : update event with a new title and no ending date")
    @Test
    public void testUpdateWhenAllDataAreValidAndEndingDateIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setEndingDate(null);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setTitle("A brand new title , Weather Hacker !!!");
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.update(toUpdate);
        }
        finally {
            Assertions.assertNotNull(result);
            Assertions.assertEquals(this.mapper.map(newEvent, EventDTO.class), result);
        }
    }

    /**
     * Event update test : check if invalid title throws IllegalArgumentException
     */
    @DisplayName("Test : update with null title throws IllegalArgumentException")
    @Test
    public void testUpdateWhenEventTitleIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setTitle(null);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : minimal players count less than 2 must throws IllegalArgumentException
     */
    @DisplayName("Test : update minimal players count less than 2 must throws IllegalArgumentException")
    @Test
    public void testUpdateWhenMinPlayerLessThan2() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setMinPlayer(1);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : minimal players count equals 2 is ok
     */
    @DisplayName("Test : update with minimal players count equals 2 is ok")
    @Test
    public void testUpdateWhenMinPlayerEquals2() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setEndingDate(null);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setMinPlayer(2);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.update(toUpdate);
        }
        finally {
            Assertions.assertNotNull(result);
            Assertions.assertEquals(this.mapper.map(newEvent, EventDTO.class), result);
        }
    }
    /**
     * Update test : minimal players count equals maximal players count is ok
     */
    @DisplayName("Test : update minimal players count equals maximal players count is ok")
    @Test
    public void testUpdateWhenMinPlayerEqualsMaxPlayer() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        event.setEndingDate(null);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setMinPlayer(newEvent.getMaxPlayer());
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.update(toUpdate);
        }
        finally {
            Assertions.assertNotNull(result);
            Assertions.assertEquals(this.mapper.map(newEvent, EventDTO.class), result);
        }
    }

    /**
     * Update test : minimal players count higher than maximal players count, throws IllegalArgumentException
     */
    @DisplayName("Test : update throws IllegalArgumentException if minimal players count higher > maximal players count")
    @Test
    public void testUpdateWhenMaxPlayerLessThanMinPlayer() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setMaxPlayer(newEvent.getMinPlayer()-1);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : description is null, throws IllegalArgumentException
     */
    @DisplayName("Test : IllegalArgumentException if description is null on update")
    @Test
    public void testUpdateWhenDescriptionIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setDescription(null);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : if the limit inscription date is before the creation date, throws IllegalArgumentException
     */
    @DisplayName("Test : IllegalArgumentException if the limit inscription date before the creation date on update")
    @Test
    public void testUpdateWhenLimitDateIsBeforeCreationDate() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setLimitDate(event.getCreationDate().minusDays(1));
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }
    /**
     * Update test : if the starting date is before the limit inscription date, throws IllegalArgumentException
     */
    @DisplayName("Test : IllegalArgumentException if the starting date before the limit inscription date on update")
    @Test
    public void testUpdateWhenStartingDateIsBeforeLimitDate() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setStartingDate(newEvent.getLimitDate().minusMonths(1));
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : if the ending date is before the starting date, throws IllegalArgumentException
     */
    @DisplayName("Test : IllegalArgumentException if the ending date before the starting date on update")
    @Test
    public void testUpdateWhenEndingDateIsBeforeStartingDate() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setEndingDate(newEvent.getStartingDate().minusDays(1));
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : game is null, throws IllegalArgumentException
     */
    @DisplayName("Test : IllegalArgumentException if game is null on update")
    @Test
    public void testUpdateWhenGameIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setGame(null);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : replacing the game of the event
     * The new game doesn't exist in database so NoSuchElementException is expected
     */
    @DisplayName("Test : change game's event with another which doesn't exist in database, NoSuchElementException")
    @Test
    public void testUpdateWhenNewGameIsNotInDatabase() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        Game newGame = this.createValidFullGame(55L, "Kingsburg");
        newEvent.setGame(newGame);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(NoSuchElementException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : replacing the game of the event
     * The new game exists in database.
     */
    @DisplayName("Test : change game's event with another which exists in database")
    @Test
    public void testUpdateWhenNewGameIsInDatabase() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event with the new game
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        Game newGame = this.createValidFullGame(55L, "Kingsburg");
        newEvent.setGame(newGame);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.gameRepository.findByUuid(newGame.getUuid())).thenReturn(Optional.of(newGame));
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.update(toUpdate);
        }
        finally {
            Assertions.assertNotNull(result);
            Assertions.assertEquals(this.mapper.map(newEvent, EventDTO.class), result);
        }
    }

    /**
     * Update test : location is null, throws IllegalArgumentException
     */
    @DisplayName("Test : IllegalArgumentException if location is null on update")
    @Test
    public void testUpdateWhenLocationIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.setLocation(null);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    /**
     * Update test : town name is null, throws IllegalArgumentException
     */
    @DisplayName("Test : IllegalArgumentException if town's name is null on update")
    @Test
    public void testUpdateWhenLocationTownIsNull() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau", null);
        Event event = this.createValidEvent(10L, game, user, location);
        location.setEvent(event);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // The new event
        Event newEvent = new Event();
        this.mapper.map(event, newEvent);
        newEvent.getLocation().setTown(null);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    // Method addUserInEvent

    /**
     * Test method addUserInEvent when given argument is null
     */
    @DisplayName("Test addUserInEvent : when given argument is null")
    @Test
    public void testAddUserInEventWithNullArgument() {
        Assertions.assertFalse(this.service.addUserInEvent(null));
    }

    /**
     * Test method addUserInEvent when event's uuid is null
     */
    @DisplayName("Test addUserInEvent : when event's uuid is null")
    @Test
    public void testAddUserInEventWithNullEventUuid() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        tuple.setNickname("toto");
        Assertions.assertFalse(this.service.addUserInEvent(tuple));
    }

    /**
     * Test method addUserInEvent when user's name is null
     */
    @DisplayName("Test addUserInEvent : when user's name is null")
    @Test
    public void testAddUserInEventWithNullUserName() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        tuple.setUuid("0c1edfd1-a240-4d0a-bc9f-93b4b5bb2e81");
        Assertions.assertFalse(this.service.addUserInEvent(tuple));
    }

    /**
     * Test method addUserInEvent when the game doesn't exist in database
     */
    @DisplayName("Test addUserInEvent : when game is not in database")
    @Test
    public void testAddUserInEventWhenGameDoesntExist() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        tuple.setNickname("toto");
        tuple.setUuid("0c1edfd1-a240-4d0a-bc9f-93b4b5bb2e81");
        Assertions.assertFalse(this.service.addUserInEvent(tuple));
    }

    /**
     * Test method addUserInEvent when the user doesn't exist in database
     */
    @DisplayName("Test addUserInEvent : when game is not in database")
    @Test
    public void testAddUserInEventWhenUserDoesntExist() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        Event event = this.createValidEvent(
                1L,
                this.createValidFullGame(1L, "Le jeu"),
                this.createValidFullUser(100L, "Bidulle"),
                new Location(100L, "Paris", null, null, null));
        tuple.setNickname("toto");
        tuple.setUuid(event.getUuid());
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        Assertions.assertFalse(this.service.addUserInEvent(tuple));
    }

    /**
     * Test method addUserInEvent when everything is fine
     */
    @DisplayName("Test addUserInEvent : when everything is fine")
    @Test
    public void testAddUserInEvent() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        User user = this.createValidFullUser(1L, "toto");
        Event event = this.createValidEvent(
                1L,
                this.createValidFullGame(1L, "Le jeu"),
                this.createValidFullUser(100L, "Bidulle"),
                new Location(100L, "Paris", null, null, null));
        tuple.setUuid(event.getUuid());
        tuple.setNickname(user.getNickname());
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        Assertions.assertTrue(this.service.addUserInEvent(tuple));
    }

    /**
     * Test method addUserInEvent when user is already in the registered list
     */
    @DisplayName("Test addUserInEvent : when user is already in the registered list")
    @Test
    public void testAddUserInEventWhenUserAlreadyInRegisteredList() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        User user = this.createValidFullUser(1L, "toto");
        Event event = this.createValidEvent(
                1L,
                this.createValidFullGame(1L, "Le jeu"),
                this.createValidFullUser(100L, "Bidulle"),
                new Location(100L, "Paris", null, null, null));
        tuple.setUuid(event.getUuid());
        tuple.setNickname(user.getNickname());
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        event.addUser(user);
        Assertions.assertFalse(this.service.addUserInEvent(tuple));
    }

    /**
     * Test method addUserInEvent when user is already in the waiting list
     */
    @DisplayName("Test addUserInEvent : when user is already in the waiting list")
    @Test
    public void testAddUserInEventWhenUserAlreadyInWaitingList() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        User user = this.createValidFullUser(1L, "toto");
        Event event = this.createValidEvent(
                1L,
                this.createValidFullGame(1L, "Le jeu"),
                this.createValidFullUser(100L, "Bidulle"),
                new Location(100L, "Paris", null, null, null));
        tuple.setUuid(event.getUuid());
        tuple.setNickname(user.getNickname());
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        event.addUserInWaitingQueue(user);
        Assertions.assertFalse(this.service.addUserInEvent(tuple));
    }

    // Method addUserInEventInWaitingQueue

    /**
     * Test method addUserInEventInWaitingQueue when given argument is null
     */
    @DisplayName("Test addUserInEventInWaitingQueue : when given argument is null")
    @Test
    public void testAddUserInEventInWaitingQueueWithNullArgument() {
        Assertions.assertFalse(this.service.addUserInEventInWaitingQueue(null));
    }

    /**
     * Test method addUserInEventInWaitingQueue when event's uuid is null
     */
    @DisplayName("Test addUserInEventInWaitingQueue : when event's uuid is null")
    @Test
    public void testAddUserInEventInWaitingQueueWithNullEventUuid() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        tuple.setNickname("toto");
        Assertions.assertFalse(this.service.addUserInEventInWaitingQueue(tuple));
    }

    /**
     * Test method addUserInEventInWaitingQueue when user's name is null
     */
    @DisplayName("Test addUserInEventInWaitingQueue : when user's name is null")
    @Test
    public void testAddUserInEventInWaitingQueueWithNullUserName() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        tuple.setUuid("0c1edfd1-a240-4d0a-bc9f-93b4b5bb2e81");
        Assertions.assertFalse(this.service.addUserInEventInWaitingQueue(tuple));
    }

    /**
     * Test method addUserInEventInWaitingQueue when the game doesn't exist in database
     */
    @DisplayName("Test addUserInEventInWaitingQueue : when game is not in database")
    @Test
    public void testAddUserInEventInWaitingQueueWhenGameDoesntExist() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        tuple.setNickname("toto");
        tuple.setUuid("0c1edfd1-a240-4d0a-bc9f-93b4b5bb2e81");
        Assertions.assertFalse(this.service.addUserInEventInWaitingQueue(tuple));
    }

    /**
     * Test method addUserInEventInWaitingQueue when the user doesn't exist in database
     */
    @DisplayName("Test addUserInEventInWaitingQueue : when game is not in database")
    @Test
    public void testAddUserInEventInWaitingQueueWhenUserDoesntExist() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        Event event = this.createValidEvent(
                1L,
                this.createValidFullGame(1L, "Le jeu"),
                this.createValidFullUser(100L, "Bidulle"),
                new Location(100L, "Paris", null, null, null));
        tuple.setNickname("toto");
        tuple.setUuid(event.getUuid());
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        Assertions.assertFalse(this.service.addUserInEventInWaitingQueue(tuple));
    }

    /**
     * Test method addUserInEventInWaitingQueue when everything is fine
     */
    @DisplayName("Test addUserInEventInWaitingQueue : when everything is fine")
    @Test
    public void testAddUserInEventInWaitingQueue() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        User user = this.createValidFullUser(1L, "toto");
        Event event = this.createValidEvent(
                1L,
                this.createValidFullGame(1L, "Le jeu"),
                this.createValidFullUser(100L, "Bidulle"),
                new Location(100L, "Paris", null, null, null));
        tuple.setUuid(event.getUuid());
        tuple.setNickname(user.getNickname());
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        Assertions.assertTrue(this.service.addUserInEventInWaitingQueue(tuple));
    }

    /**
     * Test method addUserInEventInWaitingQueue when user is already in the registered list
     */
    @DisplayName("Test addUserInEventInWaitingQueue : when user is already in the registered list")
    @Test
    public void testAddUserInEventInWaitingQueueWhenUserAlreadyInRegisteredList() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        User user = this.createValidFullUser(1L, "toto");
        Event event = this.createValidEvent(
                1L,
                this.createValidFullGame(1L, "Le jeu"),
                this.createValidFullUser(100L, "Bidulle"),
                new Location(100L, "Paris", null, null, null));
        tuple.setUuid(event.getUuid());
        tuple.setNickname(user.getNickname());
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        event.addUser(user);
        Assertions.assertFalse(this.service.addUserInEventInWaitingQueue(tuple));
    }

    /**
     * Test method addUserInEventInWaitingQueue when user is already in the waiting list
     */
    @DisplayName("Test addUserInEventInWaitingQueue : when user is already in the waiting list")
    @Test
    public void testAddUserInEventInWaitingQueueWhenUserAlreadyInWaitingList() {
        EventAddUserDTO tuple = new EventAddUserDTO();
        User user = this.createValidFullUser(1L, "toto");
        Event event = this.createValidEvent(
                1L,
                this.createValidFullGame(1L, "Le jeu"),
                this.createValidFullUser(100L, "Bidulle"),
                new Location(100L, "Paris", null, null, null));
        tuple.setUuid(event.getUuid());
        tuple.setNickname(user.getNickname());
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        event.addUserInWaitingQueue(user);
        Assertions.assertFalse(this.service.addUserInEventInWaitingQueue(tuple));
    }

    // Method removeUserInEvent

    /**
     * Test removeUserInEvent when used with a null argument
     */
    @DisplayName("Test removeUserInEvent : given argument is null")
    @Test
    public void testRemoveUserInEventWithNullArgument() {
        Assertions.assertFalse(this.service.removeUserInEvent(null));
    }

    /**
     * Test removeUserInEvent when used with a null event's uuid
     */
    @DisplayName("Test removeUserInEvent : event's uuid is null")
    @Test
    public void testRemoveUserInEventWithNullEventUuid() {
        EventRemoveUserDTO tuple = new EventRemoveUserDTO(null, "toto");
        Assertions.assertFalse(this.service.removeUserInEvent(tuple));
    }

    /**
     * Test removeUserInEvent when used with a null user's name
     */
    @DisplayName("Test removeUserInEvent : user's name is null")
    @Test
    public void testRemoveUserInEventWithNullUserName() {
        EventRemoveUserDTO tuple = new EventRemoveUserDTO("b1cdd964-dc35-4be9-9649-0db6a6afe2f1", null);
        Assertions.assertFalse(this.service.removeUserInEvent(tuple));
    }

    /**
     * Test removeUserInEvent when used with an unknown event
     */
    @DisplayName("Test removeUserInEvent : event is not in database")
    @Test
    public void testRemoveUserInEventWithUnknownEvent() {
        EventRemoveUserDTO tuple = new EventRemoveUserDTO("b1cdd964-dc35-4be9-9649-0db6a6afe2f1", "toto");
        Assertions.assertFalse(this.service.removeUserInEvent(tuple));
    }

    /**
     * Test removeUserInEvent when used with an unknown user
     */
    @DisplayName("Test removeUserInEvent : user is not in database")
    @Test
    public void testRemoveUserInEventWithUnknownUser() {
        Game game = this.createValidFullGame(5L, "Splendor");
        User user = this.createValidFullUser(5L, "nuageux");
        Location location = new Location(5L, "Paris", null, null, null);
        Event event = this.createValidEvent(5L, game, user, location);
        EventRemoveUserDTO tuple = new EventRemoveUserDTO(event.getUuid(), "toto");
        BDDMockito.when(this.repository.findByUuid(tuple.getUuid())).thenReturn(Optional.of(event));
        Assertions.assertFalse(this.service.removeUserInEvent(tuple));
    }

    /**
     * Test removeUserInEvent when given a user who is not in the registered list
     */
    @DisplayName("Test removeUserInEvent : user is not in the registered list")
    @Test
    public void testRemoveUserInEvent() {
        Game game = this.createValidFullGame(5L, "Splendor");
        User user = this.createValidFullUser(5L, "nuageux");
        Location location = new Location(5L, "Paris", null, null, null);
        Event event = this.createValidEvent(5L, game, user, location);
        EventRemoveUserDTO tuple = new EventRemoveUserDTO(event.getUuid(), user.getNickname());
        BDDMockito.when(this.repository.findByUuid(tuple.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(tuple.getNickname())).thenReturn(Optional.of(user));
        Assertions.assertFalse(this.service.removeUserInEvent(tuple));
    }

    /**
     * Test removeUserInEvent when given a user is in the registered list
     */
    @DisplayName("Test removeUserInEvent : user is in the registered list")
    @Test
    public void testRemoveUserInEventWhenUserInRegisteredList() {
        Game game = this.createValidFullGame(5L, "Splendor");
        User user = this.createValidFullUser(5L, "nuageux");
        Location location = new Location(5L, "Paris", null, null, null);
        Event event = this.createValidEvent(5L, game, user, location);
        EventRemoveUserDTO tuple = new EventRemoveUserDTO(event.getUuid(), user.getNickname());
        event.addUser(user);
        BDDMockito.when(this.repository.findByUuid(tuple.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(tuple.getNickname())).thenReturn(Optional.of(user));

        Assertions.assertTrue(event.getRegisteredUsers().contains(user));
        Assertions.assertTrue(this.service.removeUserInEvent(tuple));
        Assertions.assertFalse(event.getRegisteredUsers().contains(user));
    }

    // Method removeUserInWaitingQueue

    /**
     * Test removeUserInWaitingQueue when used with a null argument
     */
    @DisplayName("Test removeUserInWaitingQueue : given argument is null")
    @Test
    public void testRemoveUserInWaitingQueueWithNullArgument() {
        Assertions.assertFalse(this.service.removeUserInWaitingQueue(null));
    }

    /**
     * Test removeUserInWaitingQueue when used with a null event's uuid
     */
    @DisplayName("Test removeUserInWaitingQueue : event's uuid is null")
    @Test
    public void testRemoveUserInWaitingQueueWithNullEventUuid() {
        EventRemoveUserDTO tuple = new EventRemoveUserDTO(null, "toto");
        Assertions.assertFalse(this.service.removeUserInWaitingQueue(tuple));
    }

    /**
     * Test removeUserInWaitingQueue when used with a null user's name
     */
    @DisplayName("Test removeUserInWaitingQueue : user's name is null")
    @Test
    public void testRemoveUserInWaitingQueueWithNullUserName() {
        EventRemoveUserDTO tuple = new EventRemoveUserDTO("b1cdd964-dc35-4be9-9649-0db6a6afe2f1", null);
        Assertions.assertFalse(this.service.removeUserInWaitingQueue(tuple));
    }

    /**
     * Test removeUserInWaitingQueue when used with an unknown event
     */
    @DisplayName("Test removeUserInWaitingQueue : event is not in database")
    @Test
    public void testRemoveUserInWaitingQueueWithUnknownEvent() {
        EventRemoveUserDTO tuple = new EventRemoveUserDTO("b1cdd964-dc35-4be9-9649-0db6a6afe2f1", "toto");
        Assertions.assertFalse(this.service.removeUserInWaitingQueue(tuple));
    }

    /**
     * Test removeUserInWaitingQueue when used with an unknown user
     */
    @DisplayName("Test removeUserInWaitingQueue : user is not in database")
    @Test
    public void testRemoveUserInWaitingQueueWithUnknownUser() {
        Game game = this.createValidFullGame(5L, "Splendor");
        User user = this.createValidFullUser(5L, "nuageux");
        Location location = new Location(5L, "Paris", null, null, null);
        Event event = this.createValidEvent(5L, game, user, location);
        EventRemoveUserDTO tuple = new EventRemoveUserDTO(event.getUuid(), "toto");
        BDDMockito.when(this.repository.findByUuid(tuple.getUuid())).thenReturn(Optional.of(event));
        Assertions.assertFalse(this.service.removeUserInWaitingQueue(tuple));
    }

    /**
     * Test removeUserInWaitingQueue when the user is not in the waiting list
     */
    @DisplayName("Test removeUserInWaitingQueue : user is not in the waiting list")
    @Test
    public void testRemoveUserInWaitingQueue() {
        Game game = this.createValidFullGame(5L, "Splendor");
        User user = this.createValidFullUser(5L, "nuageux");
        Location location = new Location(5L, "Paris", null, null, null);
        Event event = this.createValidEvent(5L, game, user, location);
        EventRemoveUserDTO tuple = new EventRemoveUserDTO(event.getUuid(), user.getNickname());
        BDDMockito.when(this.repository.findByUuid(tuple.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(tuple.getNickname())).thenReturn(Optional.of(user));
        Assertions.assertFalse(this.service.removeUserInWaitingQueue(tuple));
    }

    /**
     * Test removeUserInWaitingQueue when given a user is in the waiting list
     */
    @DisplayName("Test removeUserInWaitingQueue : user is in the waiting list")
    @Test
    public void testRemoveUserInWaitingQueueWhenUserInWaitingList() {
        Game game = this.createValidFullGame(5L, "Splendor");
        User user = this.createValidFullUser(5L, "nuageux");
        Location location = new Location(5L, "Paris", null, null, null);
        Event event = this.createValidEvent(5L, game, user, location);
        EventRemoveUserDTO tuple = new EventRemoveUserDTO(event.getUuid(), user.getNickname());
        event.addUserInWaitingQueue(user);
        BDDMockito.when(this.repository.findByUuid(tuple.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(tuple.getNickname())).thenReturn(Optional.of(user));

        Assertions.assertTrue(event.getWaitingUsers().contains(user));
        Assertions.assertTrue(this.service.removeUserInWaitingQueue(tuple));
        Assertions.assertFalse(event.getWaitingUsers().contains(user));
    }
}