package fr.oukilson.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String uuid;
    private String name;
    private int minPlayer;
    private int maxPlayer;
    // Duration in minutes
    private int minTime;
    private int maxTime;
    private int age;
    private String creatorName;

}