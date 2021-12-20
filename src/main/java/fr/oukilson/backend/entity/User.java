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
    @Column(name = "user_id")
    private Long id;
    private String password;
    private String email;
    private String nickname;



    public User() {
    }

    public User(Long id, String password, String email, String nickname) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
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
