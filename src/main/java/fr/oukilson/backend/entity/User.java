package fr.oukilson.backend.entity;

import fr.oukilson.backend.utils.Tools;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Blob;
import java.util.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String nickname;
    private Blob icon;
//    private Set<Token> token;
//    @OneToMany
    private HashMap<Long, User> friendList;
    private HashMap<Long, User> deniedList;
//    @OneToMany//(mappedBy = "")
    private HashMap<UUID, Game> userGameList;
//    @OneToMany//(mappedBy = "")

    private HashMap<UUID, Game> userLikeList;


    public User() {
    }

    public User(Long id, String password, String email, String nickname) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    public User(Long id, String password, String firstName, String lastName, String email, String nickname, Blob icon) {
        this.id = id;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.nickname = nickname;
        this.icon = icon;
        this.friendList = new HashMap<Long, User>();
        this.deniedList = new HashMap<Long, User>();
        this.userGameList = new HashMap<UUID, Game>();
        this.userLikeList = new HashMap<UUID, Game>();
    }

    //methods
    /**
     * method to add a user-friend to main user's friend list
     * @param user a user object to be added to the list
     * @throws IllegalArgumentException if the user is already on the friend or denied list
     */
    public void addUserToFriendList(User user) throws IllegalArgumentException{
        if(!Tools.userOnList(user, this.getFriendList()) && !Tools.userOnList(user, this.getDeniedList())) {
            this.getFriendList().put(user.getId(), user);
        }
        else {
            throw new IllegalArgumentException("user already on a list");
        }
        this.setFriendList(this.getFriendList());
    }

    /**
     * method to add an unwanted user to main user's denied list
     * @param user a user object to be added to the list
     * @throws IllegalArgumentException if the user is already on the friend or denied list
     */
    public void addUserToDeniedList(User user) throws IllegalArgumentException{
        if(!Tools.userOnList(user, this.getDeniedList()) && !Tools.userOnList(user, this.getFriendList())) {
            this.getDeniedList().put(user.getId(), user);
        }
        else {
            throw new IllegalArgumentException("user already on list");
        }
        this.setDeniedList(this.getDeniedList());
    }

    /**
     * method to add a game to a user's owned game list
     * @param game to add to the list
     * @throws IllegalArgumentException if the game's already on the list
     */
    public void addGameToOwnedGameList(Game game) throws IllegalArgumentException{
        if(!Tools.gameOnList(game, this.getUserGameList())) {
            this.getUserGameList().put(game.getUUID(), game);
        }
        else {
            throw new IllegalArgumentException("you own that game already");
        }
        this.setUserGameList(this.getUserGameList());
    }

    /**
     * method to add a game to a user's liked game list
     * @param game to add to the list
     * @throws IllegalArgumentException if the game's already on the list
     */
    public void addGameToLikedGameList(Game game) throws IllegalArgumentException{
        if(!Tools.gameOnList(game, this.getUserLikeList())) {
            this.getUserLikeList().put(game.getUUID(), game);
        }
        else {
            throw new IllegalArgumentException("you like that game already");
        }
        this.setUserLikeList(this.getUserLikeList());
    }

    /**
     * method to remove a user from the friend list
     * @param user a user object to be removed from the list
     * @throws IllegalArgumentException if the user isn't on the list the main user wants to remove them from
     */
    public void removeUserFromFriendList(User user) throws IllegalArgumentException{
        if(Tools.userOnList(user, this.getFriendList())) {
            this.getFriendList().remove(user.getId());
        }
        else {
            throw new IllegalArgumentException("user is not on list.");
        }
        this.setFriendList(this.getFriendList());
    }

    /**
     * method to remove a user from the denied list
     * @param user a user object to be removed from the list
     * @throws IllegalArgumentException if the user isn't on the list the main user wants to remove them from
     */
    public void removeUserFromDeniedList(User user) throws IllegalArgumentException{
        if(Tools.userOnList(user, this.getDeniedList())) {
            this.getDeniedList().remove(user.getId());
        }
        else {
            throw new IllegalArgumentException("user is not on list.");
        }
        this.setDeniedList(this.getDeniedList());
    }

    /**
     * Method to remove a game from a user's owned game list
     * @param game game to remove from the list
     * @throws IllegalArgumentException if game isn't on the list
     */
    public void removeGameFromOwnedGameList(Game game) throws IllegalArgumentException{
        if(Tools.gameOnList(game, this.getUserGameList())) {
            this.getUserGameList().remove(game.getUUID());
        }
        else {
            throw new IllegalArgumentException("game is not on list.");
        }
        this.setUserGameList(this.getUserGameList());
    }

    /**
     * Method to remove a game from a user's liked game list
     * @param game game to remove from the list
     * @throws IllegalArgumentException if game isn't on the list
     */
    public void removeGameFromLikedGameList(Game game) throws IllegalArgumentException{
        if(Tools.gameOnList(game, this.getUserLikeList())) {
            this.getUserLikeList().remove(game.getUUID());
        }
        else {
            throw new IllegalArgumentException("game is not on list.");
        }
        this.setUserLikeList(this.getUserLikeList());
    }




    //g&s


    public Long getId() {
        return id;
    }


    /**
     * getter for user's nickname
     * @return user's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * setter for a user's nickname
     * @param nickname new user nickname
     */
    public void setNickname(String nickname) throws IllegalArgumentException{
        String regex = "^[a-zA-Z0-9_-]{4,16}$";
        if (!Tools.checkRegex(regex, nickname)) {
            throw new IllegalArgumentException("username must be valid");
        }
        else {
            this.nickname = nickname;
        }
    }

    /**
     * getter for user's email
     * @return user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * setter for a user's email
     * @param email user's new email
     */
    public void setEmail(String email) throws IllegalArgumentException{
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,5}){6,254}$";
        //"\\b[\\w.!#$%&’*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)*\\b{10,45}$";
        if (!Tools.checkRegex(regex, email)) {
            throw new IllegalArgumentException("email must be valid.");
        }
        else {
            this.email = email;
        }
    }

    /**
     * getter for user's first name
     * @return user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * setter for a user's first name
     * @param firstName a user's new first name
     * @throws IllegalArgumentException if new name is too long, too short, or generally invalid
     */
    public void setFirstName(String firstName) throws IllegalArgumentException{
        String regex = "^[a-zA-Z]{2,45}$";
        if (!Tools.checkRegex(regex, firstName)) {
            throw new IllegalArgumentException("input is invalid");
        }
        else {
            this.firstName = firstName;
        }
    }

    /**
     * getter for user's last name
     * @return user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * setter for a user's last name
     * @param lastName user's new last name(congrats on the wedding/divorce i guess)
     * @throws IllegalArgumentException if new name is too long, too short, or generally invalid
     */
    public void setLastName(String lastName) throws IllegalArgumentException{
        String regex = "^[a-zA-Z][\\-]{1}[a-zA-Z]{2,45}$";
        if (!Tools.checkRegex(regex, lastName)) {
            throw new IllegalArgumentException("input is invalid");
        }
        else {
            this.lastName = lastName;
        }
    }

    /**
     * getter for a user's friend list
     * @return a user's friend list
     */
    public HashMap<Long, User> getFriendList(){
        return friendList;
    }

    /**
     * setter for a user's friend list
     * @param friendList list of users the user's chosen as friends
     */
    public void setFriendList(HashMap<Long, User> friendList) {
        this.friendList = friendList;
    }

    /**
     * get a user's denied list
     * @return a denied list object
     */
    public HashMap<Long, User> getDeniedList() {
        return deniedList;
    }

    /**
     * set a denied list object
     * @param deniedList new object to replace the existing one
     */
    public void setDeniedList(HashMap<Long, User> deniedList) {
        this.deniedList = deniedList;
    }

    /**
     * get a user's owned game list
     * @return the list of objects
     */
    public HashMap<UUID, Game> getUserGameList() {
        return userGameList;
    }

    /**
     * set a list of owned game objects for this user
     * @param userGameList list of objects to set
     */
    public void setUserGameList(HashMap<UUID, Game> userGameList) {
        this.userGameList = userGameList;
    }

    /**
     * get a user's liked game list
     * @return a list of liked games objects for the user
     */
    public HashMap<UUID, Game> getUserLikeList() {
        return userLikeList;
    }

    /**
     * set a list of liked games objects for this user
     * @param userLikeList the list of objects to set
     */
    public void setUserLikeList(HashMap<UUID, Game> userLikeList) {
        this.userLikeList = userLikeList;
    }

    public Blob getIcon() {
        return icon;
    }

    public void setIcon(Blob icon) {
        this.icon = icon;
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
