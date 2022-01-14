package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.GameDTO;
import fr.oukilson.backend.dto.GameUuidDTO;
import fr.oukilson.backend.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    private GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    /**
     * Route to get all the game info by providing its uuid.
     * @param uuid String Uuid of the game
     * @return A GameDTO or a bad request
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<GameDTO> findByUuid(@PathVariable String uuid) {
        ResponseEntity<GameDTO> result;
        GameDTO game = this.service.findByUuid(uuid);
        if (game!=null)
            result = ResponseEntity.ok(game);
        else
            result = ResponseEntity.notFound().build();
        return result;
    }

    /**
     * Get all the games sharing the same name or at least a part of it.
     * @param game GameUuidDTO containing the string to look up in the 'name' attribute
     * @return A list of GameDTO
     */
    @PostMapping
    public ResponseEntity<List<GameUuidDTO>> findByName(@RequestBody GameUuidDTO game) {
        return ResponseEntity.ok().body(service.findByName(game));
    }
}
