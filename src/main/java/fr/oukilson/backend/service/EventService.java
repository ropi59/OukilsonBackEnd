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
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

public class EventService {
    private EventRepository repository;
    private UserRepository userRepository;
    private GameRepository gameRepository;
    private LocationRepository locationRepository;
    private ModelMapper mapper;

    public EventService(EventRepository repository, UserRepository userRepository, GameRepository gameRepository,
                        LocationRepository locationRepository, ModelMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.locationRepository = locationRepository;
        this.mapper = mapper;
    }

    /**
     * Find an event by its uuid and return all its info as a EventDTO
     * @param uuid Event's uuid
     * @return EventDTO
     */
    public EventDTO findByUuid(String uuid) {
        Event event =  this.repository.findByUuid(uuid).orElse(null);
        EventDTO result;
        if (event==null)
            result = null;
        else
            result = this.mapper.map(event, EventDTO.class);
        return result;
    }

    /**
     * Delete an event by its uuid
     * @param uuid Event's uuid
     */
    @Transactional
    public void deleteByUuid(String uuid) {
        this.repository.deleteByUuid(uuid);
    }

    /**
     * Add a new event in DB
     * @param toCreate The event to add
     * @return The created event
     */
    public EventDTO save(EventCreateDTO toCreate)
            throws NoSuchElementException, IllegalArgumentException {
        // Check data
        LocalDateTime rightNow = LocalDateTime.now();
        if (!toCreate.isValid(rightNow))
            throw new IllegalArgumentException("Event creation : Invalid parameter data.");

        // Get the user creator and the game
        Event event = this.mapper.map(toCreate, Event.class);
        event.setCreationDate(rightNow);
        event.setUuid(UUID.randomUUID().toString());
        try {
            Optional<User> user = this.userRepository.findByNickname(toCreate.getCreator().getNickname());
            event.setCreator(user.get());
            Optional<Game> game = this.gameRepository.findByUuid(toCreate.getGame().getUuid());
            event.setGame(game.get());
        }
        catch (Exception e) {
            throw new NoSuchElementException("Event creation : Unknown user/game");
        }

        // Save and return
        event.getLocation().setEvent(event);
        Location location = this.locationRepository.save(event.getLocation());
        event.setLocation(location);
        return this.mapper.map(event, EventDTO.class);
    }

    /**
     * Uppdate an existing event.
     * @param toUpdate The event to update
     * @return The updated event
     */
    public EventDTO update(EventUpdateDTO toUpdate)
            throws NoSuchElementException, IllegalArgumentException {
        // Find the event to update
        Event event = this.repository.findByUuid(toUpdate.getUuid()).orElse(null);
        if (event==null)
            throw new NoSuchElementException("Event update : Unknown event");

        // Check data
        if (!toUpdate.isValid(event.getCreationDate()))
            throw new IllegalArgumentException("Event update : Invalid parameter data.");

        // Update attribute
        String oldGameUuid = event.getGame().getUuid();
        this.mapper.map(toUpdate, event);

        // If the event's game has been modified, updated it
        if (!oldGameUuid.equals(toUpdate.getGame().getUuid())) {
            Optional<Game> optionalGame = this.gameRepository.findByUuid(toUpdate.getGame().getUuid());
            if (optionalGame.isPresent()) {
                Game game = optionalGame.get();
                event.setGame(game);
                this.repository.save(event);
            }
            else
                throw new NoSuchElementException("Event update : Unknown game");
        }
        else
            this.repository.save(event);

        return this.mapper.map(event, EventDTO.class);
    }

    /**
     * Search for events by one of this two options :
     * - date after the provided date
     * - happening in a town
     * If both filters are used, the date will be default choice
     * @param toSearch EventSearchDTO
     * @return List<EventDTO>
     */
    public List<EventDTO> findByFilter(EventSearchDTO toSearch) {
        // Get events
        List<Event> events;
        if (toSearch.getStartingDate() != null) {
            events = this.repository.findAllByStartingDateAfter(toSearch.getStartingDate());
        } else if (toSearch.getTown() != null) {
            events = this.repository.findAllByLocationTown(toSearch.getTown());
        } else
            events = new ArrayList<>();

        // Construct result
        List<EventDTO> result = new ArrayList<>();
        events.forEach(e -> result.add(this.mapper.map(e, EventDTO.class)));
        return result;
    }

    /**
     * Add a user in the event's queue.
     * Will return false if :
     * - user doesn't exist
     * - event doesn't exist
     * - user is already in the queue
     * - the event's queue is full
     * @param tuple EventAddUserDTO
     * @return True if added
     */
    public boolean addUserInEvent(EventAddUserDTO tuple) {
        boolean result;

        // Check data
        if (tuple==null || tuple.getUuid()==null || tuple.getNickname()==null)
            result = false;
        else {
            // Find user and event
            Event event = this.repository.findByUuid(tuple.getUuid()).orElse(null);
            if (event==null)
                result = false;
            else {
                User user = this.userRepository.findByNickname(tuple.getNickname()).orElse(null);
                if (user==null)
                    result = false;
                else {
                    result = event.addUser(user);
                    this.repository.save(event);
                }
            }
        }
        return result;
    }

    /**
     * Add a user in the event's waiting queue.
     * Will return false if :
     * - user doesn't exist
     * - event doesn't exist
     * - user is already in the waiting queue
     * - the event's waiting queue is full
     * @param tuple EventAddUserDTO
     * @return True if added
     */
    public boolean addUserInEventInWaitingQueue(EventAddUserDTO tuple) {
        boolean result;

        // Check data
        if (tuple==null || tuple.getUuid()==null || tuple.getNickname()==null)
            result = false;
        else {
            // Find user and event
            Event event = this.repository.findByUuid(tuple.getUuid()).orElse(null);
            if (event==null)
                result = false;
            else {
                User user = this.userRepository.findByNickname(tuple.getNickname()).orElse(null);
                if (user==null)
                    result = false;
                else {
                    result = event.addUserInWaitingQueue(user);
                    this.repository.save(event);
                }
            }
        }
        return result;
    }

    /**
     * Remove a user in an event's queue
     * Will return false if :
     * - user doesn't exist
     * - event doesn't exist
     * - user is not in the queue
     * @param tuple EventRemoveUserDTO
     * @return True if removed
     */
    public boolean removeUserInEvent(EventRemoveUserDTO tuple) {
        boolean result;

        // Check data
        if (tuple==null || tuple.getUuid()==null || tuple.getNickname()==null)
            result = false;
        else {
            // Find user and event
            Event event = this.repository.findByUuid(tuple.getUuid()).orElse(null);
            if (event==null)
                result = false;
            else {
                User user = this.userRepository.findByNickname(tuple.getNickname()).orElse(null);
                if (user==null)
                    result = false;
                else {
                    result = event.removeUser(user);
                    this.repository.save(event);
                }
            }
        }
        return result;
    }

    /**
     * Remove a user in an event's waiting queue
     * Will return false if :
     * - user doesn't exist
     * - event doesn't exist
     * - user is not in the waiting queue
     * @param tuple EventRemoveUserDTO
     * @return True if removed
     */
    public boolean removeUserInWaitingQueue(EventRemoveUserDTO tuple) {
        boolean result;

        // Check data
        if (tuple==null || tuple.getUuid()==null || tuple.getNickname()==null)
            result = false;
        else {
            // Find user and event
            Event event = this.repository.findByUuid(tuple.getUuid()).orElse(null);
            if (event==null)
                result = false;
            else {
                User user = this.userRepository.findByNickname(tuple.getNickname()).orElse(null);
                if (user==null)
                    result = false;
                else {
                    result = event.removeUserInWaitingQueue(user);
                    this.repository.save(event);
                }
            }
        }
        return result;
    }
}