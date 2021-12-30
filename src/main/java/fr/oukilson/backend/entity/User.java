package fr.oukilson.backend.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    private String email;
    private String nickname;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "friend_list",
    joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<User> friendList;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "denied_list",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "denied_id"))
    private List<User> deniedList;

    public User() {
    }

    public User(Long id, String password, String email, String nickname) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.friendList = new ArrayList<>();
        this.deniedList = new ArrayList<>();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
