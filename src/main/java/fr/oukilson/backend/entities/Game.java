package fr.oukilson.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "game")
@Data
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
    private int minTime;
    private int maxTime;
    private int minAge;
    private String creatorName;

}