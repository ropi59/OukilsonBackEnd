package fr.oukilson.backend.dto.event;

import fr.oukilson.backend.dto.game.GameUuidDTO;
import fr.oukilson.backend.dto.location.LocationDTO;
import fr.oukilson.backend.dto.user.UserNameDTO;
import fr.oukilson.backend.entity.Location;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String uuid;
    private String title;
    private UserNameDTO creator;
    private GameUuidDTO game;
    private int minPlayer;
    private int maxPlayer;
    private LocalDateTime creationDate;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private LocalDateTime limitDate;
    private String description;
    private Boolean isPrivate;
    private LocationDTO location;
    // Users registered in the event
    private List<UserNameDTO> registeredUsers = new LinkedList<>();
    // Users in the waiting queue
    private List<UserNameDTO> waitingUsers = new LinkedList<>();
}