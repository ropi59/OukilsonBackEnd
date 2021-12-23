package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.event.EventCreateDTO;
import fr.oukilson.backend.dto.event.EventDTO;
import fr.oukilson.backend.dto.event.EventUpdateDTO;
import fr.oukilson.backend.entity.Event;
import fr.oukilson.backend.repository.EventRepository;
import org.modelmapper.ModelMapper;

public class EventService {
    private EventRepository repository;
    private ModelMapper mapper;

    public EventService(EventRepository repository, ModelMapper mapper) {
        this.repository = repository;
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
    public void deleteByUuid(String uuid) {
        this.repository.deleteByUuid(uuid);
    }

    /**
     * Add a new event in DB
     * @param toCreate The event to add
     * @return The created event
     */
    public EventCreateDTO save(EventCreateDTO toCreate) {
        Event event = this.mapper.map(toCreate, Event.class);
        event = this.repository.save(event);
        return this.mapper.map(event, EventCreateDTO.class);
    }

    /**
     * Uppdate an existing event.
     * @param toUpdate The event to update
     * @return The updated event
     */
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
