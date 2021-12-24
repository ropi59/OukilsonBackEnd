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
    public ResponseEntity<GameUuidDTO> getPost(@PathVariable String uuid) {
        // J'appelle mon service pour récupérer mon article
        // L'article peut être NULL ou Rempli
        GameUuidDTO gameUuidDTO = service.findByUuid(uuid);
        // Je verifie si mon article est null
        if (gameUuidDTO == null) {
            // SI il est null
            // Je construit une RESPONSE de type 404
            return ResponseEntity.notFound().build();
        }
        // SI il est plein
        // Je construit une RESPONSE de type 200
        // AVEC l'article récupéré
        return ResponseEntity.ok().body(gameUuidDTO);
    }

}
