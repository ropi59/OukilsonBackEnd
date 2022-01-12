package fr.oukilson.backend.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
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
    private LocalDateTime creationDate;	                    // Event creation date
    @Column(name = "start_date")
    private LocalDateTime startingDate;	                    // Event starting date
    @Column(name = "end_date")
    private LocalDateTime endingDate;	                    // Event ending date
    @Column(name = "limit_date")
    private LocalDateTime limitDate;                         // End of inscription date
    private String description;	                    // Description of the event
    @Column(name = "private")
    private Boolean isPrivate;	                    // True if the event is a private event

    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;	                    // Where the event will be

    // Users registered in the event
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "event_user",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> registeredUsers = new LinkedList<>();

    // Users in the waiting queue
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "event_user_in_queue",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> waitingUsers = new LinkedList<>();


    /**
     * Check if a user is in a LinkedList.
     * Check only the id due to its uniqueness.
     * @param list The LinkedList where to check
     * @param user The user to check
     * @return True if user in list
     */
    private boolean isUserInList(List<User> list, User user) {
        boolean result = false;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            User u = (User) it.next();
            if (user.getId()==u.getId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Add a user in the event's queue
     * @param user User
     * @return True if added
     */
    public boolean addUser(User user) {
        return this.addUserInList(this.registeredUsers, this.waitingUsers, user);
    }

    /**
     * Add a user in the event's waiting queue
     * @param user User
     * @return True if added
     */
    public boolean addUserInWaitingQueue(User user) {
        return this.addUserInList(this.waitingUsers, this.registeredUsers, user);
    }

    /**
     * Add a user in a LinkedList only if :
     * - the user is not in the list 'toAdd'
     * - the user is not in the list 'exclusion'
     * - the list 'toAdd' is not full
     * @param toAdd List where to add the user
     * @param exclusion Exclusion list
     * @param user User
     * @return True if added
     */
    private boolean addUserInList(List<User> toAdd, List<User> exclusion, User user) {
        boolean result;
        if (toAdd.size()==this.maxPlayer || this.isUserInList(toAdd, user) || this.isUserInList(exclusion, user))
            result = false;
        else
            result = toAdd.add(user);
        return result;
    }

    /**
     * Remove a user in the event's queue
     * @param user User
     * @return True if removed
     */
    public boolean removeUser(User user) {
        return this.registeredUsers.removeIf(u -> u.getId()==user.getId());
    }

    /**
     * Remove a user in the event's waiting queue
     * @param user User
     * @return True if removed
     */
    public boolean removeUserInWaitingQueue(User user) {
        return this.waitingUsers.removeIf(u -> u.getId()==user.getId());
    }
}
