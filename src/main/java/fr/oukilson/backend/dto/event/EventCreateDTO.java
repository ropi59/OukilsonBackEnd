package fr.oukilson.backend.dto.event;

import fr.oukilson.backend.dto.game.GameUuidDTO;
import fr.oukilson.backend.dto.location.EventCreateLocationDTO;
import fr.oukilson.backend.dto.user.UserNameDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateDTO {
    private String title;
    private UserNameDTO creator;
    private GameUuidDTO game;
    private int minPlayer;
    private int maxPlayer;
    private LocalDateTime limitDate;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private String description;
    private Boolean isPrivate;
    private EventCreateLocationDTO location;
}