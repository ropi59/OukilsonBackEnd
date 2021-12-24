package fr.oukilson.backend.controller;

import fr.oukilson.backend.dtos.GameDTO;
import fr.oukilson.backend.dtos.GameUuidDTO;
import fr.oukilson.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("game")
public class GameController {

    @Autowired
    private GameService service;


    @GetMapping
    public List<GameDTO> findAll() {
        return this.service.findAll();
    }


    @GetMapping("{id}")
    public ResponseEntity<GameDTO> findById(@PathVariable Long id) {
        try {
            Optional<GameDTO> gameDTO = this.service.findById(id);
            return ResponseEntity.ok(gameDTO.get());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    @PostMapping
    public ResponseEntity<GameDTO> save(@RequestBody GameDTO gameDTO) {
        GameDTO response = this.service.save(gameDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<GameUuidDTO> getUuid(@PathVariable String uuid) {
        GameUuidDTO gameUuidDTO = service.findByUuid(uuid);
        if (gameUuidDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(gameUuidDTO);
    }

    @GetMapping("/display/{uuid}")
    public ResponseEntity<GameDTO> displayUuid(@PathVariable String uuid) {
        GameDTO gameDTO = service.DisplayByUuid(uuid);
        if (gameDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(gameDTO);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<GameUuidDTO> getName(@PathVariable String name) {
        GameUuidDTO gameUuidDTO = service.findByName(name);
        if (gameUuidDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(gameUuidDTO);
    }

}
