package fr.oukilson.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private int id;
    private String name;
    private int minPlayer;
    private int maxPlayer;
    private int minTime;
    private int maxTime;
    private int age;
    private String creatorName;
}
