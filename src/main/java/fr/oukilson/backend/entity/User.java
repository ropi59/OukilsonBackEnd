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

    // list methods
    public List<User> addUserToFriendList(User user) throws Exception {
        if(!this.friendList.contains(user))
            this.friendList.add(user);
        else
            throw new Exception("User is already on list");
        return this.friendList;
    }

    public List<User> removeUserFromFriendList(User user) throws Exception {
        if(this.friendList.contains(user))
            this.friendList.remove(user);
        else
            throw new Exception("User is not on list");
        return this.friendList;
    }

    public List<User> addUserToDeniedList(User user) throws Exception {
        if(!this.deniedList.contains(user))
            this.deniedList.add(user);
        else
            throw new Exception("User is already on list");
        return this.deniedList;
    }

    public List<User> removeUserFromDeniedList(User user) throws Exception {
        if(this.deniedList.contains(user))
            this.deniedList.remove(user);
        else
            throw new Exception("User is not on list");
        return this.deniedList;
    }

    public void emptyFriendList(){
        this.friendList.forEach(user -> this.friendList.remove(user));
    }

    public void emptyDeniedList(){
        this.deniedList.forEach(user -> this.deniedList.remove(user));
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
