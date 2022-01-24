package fr.oukilson.backend.dto.game;

import lombok.*;

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