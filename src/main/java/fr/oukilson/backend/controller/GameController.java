package fr.oukilson.backend.controller;

import fr.oukilson.backend.dtos.GameUuidDTO;
import fr.oukilson.backend.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("game")
public class GameController {
    private GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    /**
     * Route to get all the game info by providing its id.
     * @param uuid Uuid of the game
     * @return ResponseEntity<GameUuidDTO>
     */
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

    /**
     * Get all the games sharing the same name or at least a part of it.
     * @param game GameUuidDTO containing the string to look up in the 'name' attribute
     * @return ResponseEntity<List<GameUuidDTO>>
     */
    @PostMapping
    public ResponseEntity<List<GameUuidDTO>> findByName(@RequestBody GameUuidDTO game) {
        return ResponseEntity.ok().body(service.findByName(game));
    }
}
