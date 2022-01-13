package fr.oukilson.backend.entities;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "game")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private String name;
    private Integer minPlayer;
    private Integer maxPlayer;
    // Duration in minutes
    private Integer minTime;
    private Integer maxTime;
    private Integer minAge;
    private String creatorName;

}