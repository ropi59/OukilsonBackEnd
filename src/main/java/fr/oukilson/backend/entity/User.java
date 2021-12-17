package fr.oukilson.backend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.List;


import java.util.*;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@user_id")
public class User {

    @Id
    private String id;
    private String password;
    private String firstName;
    private String lastName;
    private String email; //TODO test unicité du mail
    private String nickname; //TODO test unicité du nickname
    private Blob icon;

}
