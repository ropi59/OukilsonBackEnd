package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.event.*;
import fr.oukilson.backend.service.EventService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    /**
     * Route to get the info of the event from its uuid
     * @param uuid Event's uuid
     * @return EventDTO
     */
    @GetMapping("{uuid}")
    public ResponseEntity<EventDTO> findByUuid(@PathVariable String uuid) {
        EventDTO eventDTO = service.findByUuid(uuid);
        ResponseEntity<EventDTO> result;
        if (eventDTO==null)
            result = ResponseEntity.notFound().build();
        else
            result = ResponseEntity.ok(eventDTO);
        return result;
    }

    /**
     * Search for an event by one of this two options :
     * - date after the provided date
     * - happening in a town
     * @param toSearch EventSearchDTO
     * @return List<EventDTO>
     */
    @ResponseBody
    @GetMapping("/search")
    public List<EventDTO> findAllByFilters(@RequestBody EventSearchDTO toSearch) {
        return this.service.findByFilter(toSearch);
    }

    /**
     * Route to create a new event
     * @param toCreate Event to create
     * @return The created event
     */
    @PostMapping
    public ResponseEntity<EventCreateDTO> save(@RequestBody EventCreateDTO toCreate) {
        ResponseEntity<EventCreateDTO> result;
        try {
            EventCreateDTO event = this.service.save(toCreate);
            result = ResponseEntity.status(HttpStatus.CREATED).body(event);
        }
        catch(Exception e) {
            result = ResponseEntity.badRequest().build();
        }
        return result;
    }

    /**
     * Route to update an existing event using its uuid
     * @param toUpdate The event to update
     * @return The updated event
     */
    @PutMapping
    public ResponseEntity<EventUpdateDTO> updateById(@RequestBody EventUpdateDTO toUpdate) {
        return ResponseEntity.ok(this.service.update(toUpdate));
    }

    /**
     * Route to delete the event by its uuid
     * @param toDelete EventDeleteDTO
     * @return Always true
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteByUuid(@RequestBody EventDeleteDTO toDelete) {
        this.service.deleteByUuid(toDelete.getUuid());
        return ResponseEntity.ok(true);
    }
}
