package fr.oukilson.backend.entity;

import lombok.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="event")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                // DB id
    private String uuid;                            // String uuid to access from the client
    private String title;                           // Event's title

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;		                    // The user who creates this event

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;		        	            // Game the event is about

    @Column(name = "min_player")
    private int minPlayer;		                    // Minimum number of players to run the event
    @Column(name = "max_player")
    private int maxPlayer;		                    // Maximum number of players to run the event
    @Column(name = "creation_date")
    private Date creationDate;	                    // Event creation date
    @Column(name = "start_date")
    private Date startingDate;	                    // Event starting date
    @Column(name = "end_date")
    private Date endingDate;	                    // Event ending date
    @Column(name = "limit_date")
    private Date limitDate;                         // End of inscription date
    private String description;	                    // Description of the event
    @Column(name = "private")
    private Boolean isPrivate;	                    // True if the event is a private event

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;	                    // Where the event will be

    // Users registered in the event
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "event_user",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> registeredUsers = new ArrayList<>();

    // Users in the waiting queue
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "event_user_in_queue",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> waitingUsers = new ArrayList<>();
}
