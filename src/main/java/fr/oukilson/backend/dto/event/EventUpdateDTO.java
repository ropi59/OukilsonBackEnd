package fr.oukilson.backend.dto.event;

import fr.oukilson.backend.dto.game.GameUuidDTO;
import fr.oukilson.backend.dto.location.EventUpdateLocationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDTO {
    private String uuid;
    private String title;
    private GameUuidDTO game;
    private int minPlayer;
    private int maxPlayer;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private LocalDateTime limitDate;
    private String description;
    private Boolean isPrivate;
    private EventUpdateLocationDTO location;
}