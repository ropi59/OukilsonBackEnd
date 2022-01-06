package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.event.EventCreateDTO;
import fr.oukilson.backend.dto.event.EventDTO;
import fr.oukilson.backend.dto.event.EventSearchDTO;
import fr.oukilson.backend.dto.event.EventUpdateDTO;
import fr.oukilson.backend.entity.Event;
import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.EventRepository;
import fr.oukilson.backend.repository.GameRepository;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

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
    public EventCreateDTO save(EventCreateDTO toCreate)
            throws NoSuchElementException, IllegalArgumentException {
        // Check data
        if (isCreateEventDTOValid(toCreate)==false)
            throw new IllegalArgumentException("Event creation : Invalid parameter data.");

        // Get the user creator and the game
        Event event = this.mapper.map(toCreate, Event.class);
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
        event.setCreationDate(new Date());

        // Save and return
        event = this.repository.save(event);
        return this.mapper.map(event, EventCreateDTO.class);
    }

    /**
     * Check constraints on creation DTO :
     * 1 - Minimum number of players >= 2
     * 2 - Maximal number of players >= Minimum number of players
     * 3 - Limit inscription date must be after starting event date and ending event date (if any)
     * 4 - If there's an ending event date, then it'll come after the starting event date
     * @param event EventCreateDTO to create
     * @return True id EventCreateDTO parameter is valid
     */
    private boolean isCreateEventDTOValid(EventCreateDTO event) {
        if (event.getMinPlayer()>=2
                && event.getMaxPlayer()>=event.getMinPlayer()
                && event.getLimitDate().before(event.getStartingDate())
                && (event.getEndingDate()==null || event.getStartingDate().before(event.getEndingDate()))
        )
            return true;
        else
            return false;
    }

    /**
     * Check constraints on update DTO :
     * 1 - Minimum number of players >= 2
     * 2 - Maximal number of players >= Minimum number of players
     * 3 - Limit inscription date must be after starting event date and ending event date (if any)
     * 4 - If there's an ending event date, then it'll come after the starting event date
     * @param event EventCreateDTO to create
     * @return True id EventCreateDTO parameter is valid
     */
    private boolean isUpdateEventDTOValid(EventUpdateDTO event) {
        if (event.getMinPlayer()>=2
                &&event.getMaxPlayer()>=event.getMinPlayer()
                && event.getLimitDate().before(event.getStartingDate())
                && (event.getEndingDate()==null || event.getStartingDate().before(event.getEndingDate()))
        )
            return true;
        else
            return false;
    }

    /**
     * Uppdate an existing event.
     * @param toUpdate The event to update
     * @return The updated event
     */
    public EventUpdateDTO update(EventUpdateDTO toUpdate)
            throws NoSuchElementException, IllegalArgumentException {
        // Check data
        if (isUpdateEventDTOValid(toUpdate)==false)
            throw new IllegalArgumentException("Event creation : Invalid parameter data.");

        // Find event
        Event event = this.repository.findByUuid(toUpdate.getUuid()).orElse(null);
        if (event==null)
            throw new NoSuchElementException("Event creation : Unknown event");
        String oldGameUuid = event.getGame().getUuid();
        this.mapper.map(toUpdate, event);

        // Replace game if needed
        if (!oldGameUuid.equals(toUpdate.getGame().getUuid())) {
            try {
                Optional<Game> game = this.gameRepository.findByUuid(toUpdate.getGame().getUuid());
                event.setGame(game.get());
            } catch (Exception e) {
                throw new NoSuchElementException("Event creation : Unknown game");
            }
        }

        // Save and return
        return this.mapper.map(this.repository.save(event), EventUpdateDTO.class);
    }

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
}