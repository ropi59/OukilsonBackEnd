package fr.oukilson.backend.dto.event;

import fr.oukilson.backend.dto.game.GameUuidDTO;
import fr.oukilson.backend.dto.user.UserNameDTO;
import fr.oukilson.backend.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateDTO {
    private String title;
    private UserNameDTO creator;
    private GameUuidDTO game;
    private int minPlayer;
    private int maxPlayer;
    private Date limitDate;
    private Date startingDate;
    private Date endingDate;
    private String description;
    private Boolean isPrivate;
    private Location location;
}