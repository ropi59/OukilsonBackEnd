package fr.oukilson.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private String uuid;
    private String name;
    private int minPlayer;
    private int maxPlayer;
    private int minPlayingTime;
    private int maxPlayingTime;
    private int minAge;
    private String creatorName;
}
