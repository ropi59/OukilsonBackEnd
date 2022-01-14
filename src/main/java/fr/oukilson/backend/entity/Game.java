package fr.oukilson.backend.entity;

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
    private int minPlayer;
    private int maxPlayer;
    // Duration in minutes
    private int minPlayingTime;
    private int maxPlayingTime;
    private int minAge;
    private String creatorName;
}