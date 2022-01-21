package fr.oukilson.backend.entity;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // DB id
    private String nickname;            // Unique username; also used to access from the client
    private String password;            // Encrypted version of the password
    private String email;               // Email of the user
    @Column(name = "first_name")
    private String firstName;           // First name of the user
    @Column(name = "last_name")
    private String lastName;            // Last name of the user

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "friend_list",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<User> friendList = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "denied_list",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "denied_id"))
    private List<User> deniedList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}