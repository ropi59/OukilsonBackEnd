package fr.oukilson.backend.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name="game")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // DB id
    private String uuid;                // String uuid to access from the client
    private String name;                // Game's name
    @Column(name = "min_player")
    private Integer minPlayer;          // Minimal number of players
    @Column(name = "max_player")
    private Integer maxPlayer;          // Maximal number of players
    @Column(name = "min_time")
    private Integer minPlayingTime;     // Average minimal time for a party
    @Column(name = "max_time")
    private Integer maxPlayingTime;     // Average maximal time for a party
    @Column(name = "min_age")
    private Integer minAge;             // Recommended minimal age to play
    @Column(name = "creator_name")
    private String creatorName;         // Creator's name of the game
}