package fr.oukilson.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private String uuid;
    private String name;
    private Integer minPlayer;
    private Integer maxPlayer;
    private Integer minTime;
    private Integer maxTime;
    private Integer minAge;
    private String creatorName;
}
