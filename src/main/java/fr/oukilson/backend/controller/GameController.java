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

    @GetMapping("/{uuid}")
    public ResponseEntity<GameUuidDTO> findByUuid(@PathVariable String uuid) {
        ResponseEntity<GameUuidDTO> result;
        Optional<GameUuidDTO> gameUuidDTO = this.service.findByUuid(uuid);
        if (gameUuidDTO.isPresent())
            result = ResponseEntity.ok(gameUuidDTO.get());
        else
            result = ResponseEntity.notFound().build();
        return result;
    }

    @PostMapping
    public ResponseEntity<List<GameUuidDTO>> findByName(@RequestBody GameUuidDTO game) {
        return ResponseEntity.ok().body(service.findByName(game));
    }
}
