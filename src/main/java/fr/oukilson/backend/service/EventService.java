package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.event.EventCreateDTO;
import fr.oukilson.backend.dto.event.EventDTO;
import fr.oukilson.backend.dto.event.EventUpdateDTO;
import fr.oukilson.backend.entity.Event;
import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.EventRepository;
import fr.oukilson.backend.repository.GameRepository;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class EventService {
    private EventRepository repository;
    private UserRepository userRepository;
    private GameRepository gameRepository;
    private ModelMapper mapper;

    public EventService(EventRepository repository, UserRepository userRepository, GameRepository gameRepository, ModelMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
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
    public EventCreateDTO save(EventCreateDTO toCreate) throws NoSuchElementException {
        Event event = this.mapper.map(toCreate, Event.class);
        event.setUuid(UUID.randomUUID().toString());

        // Get the user by its nickname
        Optional<User> user = this.userRepository.findByNickname(toCreate.getCreator().getNickname());
        try {
            event.setCreator(user.get());
        }
        catch (Exception e) {
            throw new NoSuchElementException("Event creation : Unknown user");
        }

        // Get the game by its uuid
        Optional<Game> game = this.gameRepository.findByUuid(toCreate.getGame().getUuid());
        try {
            event.setGame(game.get());
        }
        catch (Exception e) {
            throw new NoSuchElementException("Event creation : Unknown game");
        }

        // Save and return
        event = this.repository.save(event);
        return this.mapper.map(event, EventCreateDTO.class);
    }

    /**
     * Uppdate an existing event.
     * @param toUpdate The event to update
     * @return The updated event
     */
    // TODO
    public EventUpdateDTO update(EventUpdateDTO toUpdate) {
        Event event = this.repository.findByUuid(toUpdate.getUuid()).orElse(null);
        EventUpdateDTO result = null;
        if (event!=null) {
            this.mapper.map(toUpdate, event);
            this.repository.save(event);
            result = this.mapper.map(event, EventUpdateDTO.class);
        }
        return result;
    }
}
