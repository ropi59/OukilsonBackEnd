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
public class EventUpdateDTO {
    private String uuid;
    private String title;
    private GameUuidDTO game;
    private int minPlayer;
    private int maxPlayer;
    private Date creationDate;
    private Date startingDate;
    private Date endingDate;
    private Date limitDate;
    private String description;
    private Boolean isPrivate;
    private Location location;
}