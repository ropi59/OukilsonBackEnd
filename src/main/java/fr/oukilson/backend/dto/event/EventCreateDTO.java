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
    private boolean isPrivate;
    private EventCreateLocationDTO location;

    /**
     * An EventCreateDTO is valid when all the conditions are respected :
     * - Minimum number of players >= 2
     * - Maximal number of players >= Minimum number of players
     * - Event's title must be not null
     * - Event's description must be not null
     * - Event's creator name must be not null
     * - Event's game uuid must be not null
     * - at least, the attribute 'town' of the attribute 'location' is not null
     * - limitDate must be after the provided parameter date
     * - startingDate must equal or after limitDate
     * - endingDate can be null but, if not, must be after startingDate
     * @param date A LocalDateTime to check the validity of date type attributes
     * @return True if valid
     */
    public boolean isValid(LocalDateTime date) {
        boolean result;
        result = this.minPlayer >= 2 && this.minPlayer <= this.maxPlayer && this.title != null && this.description != null
                && this.creator != null && this.creator.getNickname() != null && this.game != null
                && this.game.getUuid() != null && this.location != null && this.location.getTown() != null
                && this.limitDate != null && !this.limitDate.isBefore(date) && this.startingDate != null
                && !this.startingDate.isBefore(this.limitDate)
                && (this.endingDate == null || !this.endingDate.isBefore(this.startingDate));
        return result;
    }
}