package fr.oukilson.backend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.List;


import java.util.*;

@Entity
@Table(name = "user")
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


    /*
    if needs to be ordered we use a list because it has methods(?) to get first & last
    so it's good if we also just need to get a FIFO(PEPS)
    otherwise using a hashmap is better because it's faster (don't need to run through the
    entire list and then compare with the object, just compare with the key)
     */
    private HashMap<UUID, User> friendList;
    private HashMap<UUID, User> deniedList;


    /**
     * empty constructor
     */
    public User() {
    }

    /**
     * basic constructor for testing purposes
     *
     * @param nickname user nickname
     */

    public User(String nickname) {
        this.nickname = nickname;
    }

    /**
     * constructor with nick & email, the two basic parameters at account creation
     *
     * @param nickname user chosen nickname
     * @param email    user's email address
     * @throws IllegalArgumentException if the nickname/email is either too short or long or invalid etc
     */
    public User(String nickname, String email) throws IllegalArgumentException {
        this.setNickname(nickname);
        this.setEmail(email);
        this.friendList = new HashMap<>();
        this.deniedList = new HashMap<>();

    }

    /**
     * constructor with 3 arguments
     *
     * @param firstName user's first name, can be null
     * @param email     user's email
     * @param nickname  user's nickname
     * @throws IllegalArgumentException too long/short, invalid input
     */
    public User(String nickname, String email, String firstName) throws IllegalArgumentException {
        this(nickname, email);
        this.friendList = new HashMap<>();
        this.deniedList = new HashMap<>();
    }

    /**
     * constructor with 4 arguments, instantiates lists
     *
     * @param nickname  user's nickname
     * @param email     user's email
     * @param firstName user's first name, can be null
     * @param lastName  user's last name, can be null
     * @throws IllegalArgumentException too long or too short or invalid input
     */
    public User(String nickname, String email, String firstName, String lastName) throws IllegalArgumentException {
        this(nickname, email, firstName);
        this.setLastName(lastName);


    }


    //g&s
    public java.util.UUID getUUID() {
        return UUID;
    }

    public void setUUID(java.util.UUID UUID) {
        this.id = ;
    }

    /**
     * getter for user's nickname
     *
     * @return user's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * setter for a user's nickname
     *
     * @param nickname new user nickname
     */

    public void setNickname(String nickname) throws IllegalArgumentException {
        String regex = "^[a-zA-Z0-9_-]{3,45}$";
        if (!Tools.checkRegex(regex, nickname)) {
            throw new IllegalArgumentException("username must be valid");
        } else {
            this.nickname = nickname;
        }
    }

    /**
     * getter for user's email
     *
     * @return user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * setter for a user's email
     *
     * @param email user's new email
     */
    public void setEmail(String email) throws IllegalArgumentException {
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,}){10,45}$";
        //"\\b[\\w.!#$%&’*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)*\\b{10,45}$";
        if (!Tools.checkRegex(regex, email)) {
            throw new IllegalArgumentException("email must be valid.");
        } else {
            this.email = email;
        }
    }

    /**
     * getter for user's first name
     *
     * @return user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * setter for a user's first name
     *
     * @param firstName a user's new first name
     * @throws IllegalArgumentException if new name is too long, too short, or generally invalid
     */
    public void setFirstName(String firstName) throws IllegalArgumentException {
        String regex = "^[a-zA-Z]{2,45}$";
        if (!Tools.checkRegex(regex, firstName)) {
            throw new IllegalArgumentException("input is invalid");
        } else {
            this.firstName = firstName;
        }
    }

    /**
     * getter for user's last name
     *
     * @return user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * setter for a user's last name
     *
     * @param lastName user's new last name(congrats on the wedding/divorce i guess)
     * @throws IllegalArgumentException if new name is too long, too short, or generally invalid
     */
    public void setLastName(String lastName) throws IllegalArgumentException {
        String regex = "^[a-zA-Z]{2,45}$";
        if (!Tools.checkRegex(regex, lastName)) {
            throw new IllegalArgumentException("input is invalid");
        } else {
            this.lastName = lastName;
        }
    }

}
