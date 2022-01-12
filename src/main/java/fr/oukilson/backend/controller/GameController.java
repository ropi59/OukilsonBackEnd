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

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<GameUuidDTO> findByUuid(@PathVariable String uuid) {
        try {
            Optional<GameUuidDTO> gameUuidDTO = this.service.findByUuid(uuid);
            return ResponseEntity.ok(gameUuidDTO.get());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    @GetMapping("/display/{uuid}")
    public ResponseEntity<GameDTO> displayByUuid(@PathVariable String uuid) {
        try {
            Optional<GameDTO> gameDTO = this.service.displayByUuid(uuid);
            return ResponseEntity.ok(gameDTO.get());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<GameUuidDTO>> findByName(@PathVariable String name) {
        List<GameUuidDTO> gameUuidDTO = service.findByName(name);
        if (gameUuidDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(gameUuidDTO);
    }



}
