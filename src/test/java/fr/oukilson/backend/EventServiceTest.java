package fr.oukilson.backend;

import fr.oukilson.backend.dto.event.EventCreateDTO;
import fr.oukilson.backend.dto.event.EventDTO;
import fr.oukilson.backend.dto.event.EventSearchDTO;
import fr.oukilson.backend.dto.event.EventUpdateDTO;
import fr.oukilson.backend.entity.Event;
import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.entity.Location;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.EventRepository;
import fr.oukilson.backend.repository.GameRepository;
import fr.oukilson.backend.repository.LocationRepository;
import fr.oukilson.backend.repository.UserRepository;
import fr.oukilson.backend.service.EventService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.AdditionalMatchers;
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
        Location location = new Location(1L, "Euralille", "59777", "1 Place François Mitterrand");
        Event event = this.createValidEvent(1L, game, user, location);
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
    @DisplayName("Test : find all events from a certain town")
    @Test
    public void testFindAllEventsByTown() {
        // Mock 2 events in town Pau
        Location location = new Location(1L, "Pau", "64000", "Rue Mathieu Lalanne");
        List<Event> list = new ArrayList<>();
        int size = 2;
        for (int i=0; i<size; i++) {
            User user = this.createValidFullUser((long) i, "tata000"+i);
            Game game = this.createValidFullGame((long) i, "The game V"+i);
            Event event = this.createValidEvent((long) i, game, user, location);
            list.add(event);
        }
        BDDMockito.when(this.repository.findAllByLocationTown(location.getTown())).thenReturn(list);

        // Anything other than the town location should return an empty list
        BDDMockito
                .when(this.repository.findAllByLocationTown(
                        AdditionalMatchers.not(ArgumentMatchers.eq(location.getTown()))))
                .thenReturn(new ArrayList<>());
        BDDMockito
                .when(this.repository.findAllByStartingDateAfter(ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // Search request
        EventSearchDTO toSearch = new EventSearchDTO();
        toSearch.setTown(location.getTown());
        List<EventDTO> result = this.service.findByFilter(toSearch);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), size);
        for (int i=0; i<size; i++) {
            Assertions.assertEquals(this.mapper.map(list.get(i), EventDTO.class), result.get(i));
        }
    }

    /**
     * Testing for search event method by giving a date filter.
     * Must return all events after the given date.
     */
    @DisplayName("Test : find all events after a given date")
    @Test
    public void testFindAllEventsByDateAfter() {
        // Mock 3 events in 2156
        List<Event> list = new ArrayList<>();
        int size = 3;
        for (int i=0; i<size; i++) {
            User user = this.createValidFullUser((long) i, "tata000"+i);
            Game game = this.createValidFullGame((long) i, "The game V"+i);
            Location location = new Location((long) i, "Ville n°"+i, "XXXX"+i, i+" rue du Jeu");
            Event event = this.createValidEvent((long) i, game, user, location);
            event.setStartingDate(event.getStartingDate().withYear(2156));
            event.setEndingDate(event.getEndingDate().withYear(2156));
            list.add(event);
        }
        LocalDateTime date = LocalDateTime.now();
        BDDMockito.when(this.repository.findAllByStartingDateAfter(date)).thenReturn(list);

        // Mock empty list for any search by town
        BDDMockito
                .when(this.repository.findAllByLocationTown(ArgumentMatchers.anyString()))
                .thenReturn(new ArrayList<>());

        // Search request
        EventSearchDTO toSearch = new EventSearchDTO();
        toSearch.setStartingDate(date);
        List<EventDTO> result = this.service.findByFilter(toSearch);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), size);
        for(int i=0; i<size; i++) {
            Assertions.assertEquals(this.mapper.map(list.get(i), EventDTO.class), result.get(i));
        }
    }

    /**
     * Testing for search event method by giving no filters.
     * Should return an empty list
     */
    @DisplayName("Test : search when all filters are null")
    @Test
    public void testFindAllEventsWithNoDateAndNoTown() {
        EventSearchDTO toSearch = new EventSearchDTO();
        List<EventDTO> result = this.service.findByFilter(toSearch);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Testing for search event method by giving both town and date filters.
     * If both are present, then the date filter takes priority.
     */
    @DisplayName("Test : find events when town and date filters are initialized")
    @Test
    public void testFindAllEventsWithBothDateAndTownGiven() {
        // Mock 3 events in 2156
        List<Event> futureEvents = new ArrayList<>();
        int futureEventNb = 3;
        int i = 0;
        while (i<futureEventNb) {
            User user = this.createValidFullUser((long) i, "tata000"+i);
            Game game = this.createValidFullGame((long) i, "The game V"+i);
            Location location = new Location((long) i, "Ville n°"+i, "XXXX"+i, i+" rue du Jeu");
            Event event = this.createValidEvent((long) i, game, user, location);
            event.setStartingDate(event.getStartingDate().withYear(2156));
            event.setEndingDate(event.getEndingDate().withYear(2156));
            futureEvents.add(event);
            i++;
        }
        LocalDateTime date = LocalDateTime.now();
        BDDMockito.when(this.repository.findAllByStartingDateAfter(date)).thenReturn(futureEvents);

        // Mock 2 events in Paris
        int parisEventNb = 2;
        List<Event> parisEvents = new ArrayList<>();
        Location location = new Location(50L, "Paris", "75012", "Place Louis-Armand");
        while (i<(parisEventNb+futureEventNb)) {
            User user = this.createValidFullUser((long) i, "tata000"+i);
            Game game = this.createValidFullGame((long) i, "The game V"+i);
            Event event = this.createValidEvent((long) i, game, user, location);
            parisEvents.add(event);
            i++;
        }
        BDDMockito.when(this.repository.findAllByLocationTown(location.getTown())).thenReturn(parisEvents);

        // Mock empty list for any search by town
        BDDMockito
                .when(this.repository.findAllByLocationTown(ArgumentMatchers.anyString()))
                .thenReturn(new ArrayList<>());

        // Search request
        EventSearchDTO toSearch = new EventSearchDTO(date, location.getTown());
        List<EventDTO> result = this.service.findByFilter(toSearch);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(futureEventNb, result.size());
        for (int j=0; j<futureEventNb; j++) {
            Assertions.assertEquals(this.mapper.map(futureEvents.get(j), EventDTO.class), result.get(j));
        }
    }

    // method save

    /**
     * Testing correct event creation
     */
    @DisplayName("Test : create an event with valid data")
    @Test
    public void testSaveWhenAllDataAreValidAndPrivateIsFalse() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setEndingDate(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setTitle(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setMinPlayer(1);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setMinPlayer(2);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setMinPlayer(3);
        event.setMaxPlayer(event.getMinPlayer());
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setMaxPlayer(event.getMinPlayer()-1);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setDescription(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setLimitDate(event.getCreationDate().minusYears(1));
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setStartingDate(event.getLimitDate().minusMonths(1));
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setEndingDate(event.getStartingDate().minusDays(1));
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setCreator(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Test event creation with a username not in database
     */
    @DisplayName("Test : can't find creator user in database")
    @Test
    public void testSaveWhenCreatorIsNotInDatabase() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setGame(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Testing creation event with a game not in database
     */
    @DisplayName("Test : game for event not in database")
    @Test
    public void testSaveWhenGameIsNotInDatabase() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setLocation(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.getLocation().setTown(null);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // Assert
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.save(toCreate));
    }

    /**
     * Test event creation with a location which doesn't exist in database
     */
    @DisplayName("Test : event creation with a new location")
    @Test
    public void testSaveWhenLocationIsNotInDatabase() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));

        // Save
        EventCreateDTO toCreate = this.mapper.map(event, EventCreateDTO.class);

        // Assert
        EventDTO result = null;
        try {
            result = this.service.save(toCreate);
        }
        finally {
            Assertions.assertNotNull(result);
            Assertions.assertEquals(this.mapper.map(event, EventDTO.class), result);
        }
    }

    // method update

    /**
     * Update event testing
     */
    @DisplayName("Test : update event with a new title")
    @Test
    public void testUpdateWhenAllDataAreValidAndPrivateIsFalse() {
        // Mock event
        Game game = this.createValidFullGame(10L, "Innovation");
        User user = this.createValidFullUser(10L, "SuperAlbert");
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
        newEvent.setTitle("A brand new title , Weather Hacker !!!");
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        result = this.service.update(toUpdate);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setEndingDate(null);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
        newEvent.setTitle("A brand new title , Weather Hacker !!!");
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        result = this.service.update(toUpdate);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setEndingDate(null);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
        newEvent.setMinPlayer(2);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        result = this.service.update(toUpdate);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        event.setEndingDate(null);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
        newEvent.setMinPlayer(newEvent.getMaxPlayer());
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        EventDTO result = null;
        result = this.service.update(toUpdate);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
        newEvent.setGame(null);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    @Test
    public void testUpdateWhenNewGameIsNotInDatabase() {
        Assertions.fail();
    }

    @Test
    public void testUpdateWhenNewGameIsInDatabase() {
        Assertions.fail();
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
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
        Location location = new Location(10L, "Gan", "64290", "123 Rue d'Ossau");
        Event event = this.createValidEvent(10L, game, user, location);
        BDDMockito.when(this.repository.findByUuid(event.getUuid())).thenReturn(Optional.of(event));
        BDDMockito.when(this.userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        BDDMockito.when(this.gameRepository.findByUuid(game.getUuid())).thenReturn(Optional.of(game));
        BDDMockito
                .when(this.locationRepository
                        .findByTownAndZipCodeAndAddress(location.getTown(), location.getZipCode(), location.getAddress()))
                .thenReturn(Optional.of(location));

        // The new event
        Event newEvent = this.mapper.map(event, Event.class);
        newEvent.getLocation().setTown(null);
        EventUpdateDTO toUpdate = this.mapper.map(newEvent, EventUpdateDTO.class);
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Event.class))).thenReturn(newEvent);

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(toUpdate));
    }

    @Test
    public void testUpdateWhenNewLocationIsNotInDatabase() {
        Assertions.fail();
    }

    @Test
    public void testUpdateWhenNewLocationIsInDatabase() {
        Assertions.fail();
    }
}