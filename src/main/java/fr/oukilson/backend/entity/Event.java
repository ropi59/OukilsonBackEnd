package fr.oukilson.backend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.*;

/**
 * A server-sided representation of a game event
 */
@Entity
@Table(name = "event")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "event_id")
public class Event {

	@Id
	private String id;
	private String title;		// Event's title
	private UUID eventUUID;		// Event's UUID (do not share DB Id, use this instead)
	private Date creationDate;	// Event creation date
	private Date startingDate;	// Event starting date
	private boolean isPrivate;	// True if the event is a private event
	private int dbId;			// Event Id in the database

	private User creator;		// The user who creates this event
	private int minPlayer;		// Minimum number of players to run the event
	private int maxPlayer;		// Maximum number of players to run the event
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "location_id")
	private Location location;	// Where the event will be
	private Map<UUID, User> registeredUsers; 	// Users registered in the event
	private Map<UUID, User> waitingUsers;	// Users in the waiting queue
	
	
	// Can be null
	private String description;	// Description of the event
	private Date endingDate;	// Event creation date (if null, then endingDate = startingDate)
	
	
	/**
	 * Constructor
	 * @param title Title of the event
	 * @param creationDate When the event was created
	 * @param startingDate When the event begins
	 * @param minPlayer Minimum players
	 * @param maxPlayer Maximum players
	 * @param isPrivate Is the event private?
	 */
	public Event(String title, Date creationDate, Date startingDate, int minPlayer, int maxPlayer, boolean isPrivate) 
			throws IllegalArgumentException{
		super();
		this.setTitle(title);
		this.setStartAndEndDate(startingDate, endingDate);
		this.setPrivate(isPrivate);
		this.setPlayersNumber(minPlayer, maxPlayer);
		this.registeredUsers = new HashMap<UUID, User>();
		this.waitingUsers = new HashMap<UUID, User>();
	}
	
	/**
	 * Constructor with only the event's title.
	 * Default value :
	 * - creation date, staring date = NOW
	 * - min & max players = 2
	 * - private event? = false
	 * @param title Title of the event
	 */
	public Event(String title) {
		this(title, new Date(), new Date(), 2, 2, false);
	}
	
	/**
	 * Setting up the minimal and maximal numbers of players for the event.
	 * @param min Minimum number of players
	 * @param max Maximum number of players
	 */
	public void setPlayersNumber(int min, int max) throws IllegalArgumentException {
		if (min<2 || max<2) throw new IllegalArgumentException("All parameters must be >= 2.");
		else
		if (min>max) throw new IllegalArgumentException("Minimum player must be inferior or equal to maximum player.");
		else {
			this.minPlayer = min;
			this.maxPlayer = max;
		}
	}	
	
	/**
	 * Setting up the minimal and maximal numbers of players for the event.
	 * Use this when both number are the same
	 * @param minmax Number of players
	 */
	public void setPlayersNumber(int minmax) {
		this.setPlayersNumber(minmax, minmax);
	}
	
	/**
	 * Add an User into the event waiting queue list.
	 * Return true if the User has been added to the event queue.
	 * If there is not enough places (aka max player count reached), then return false.
	 * @param u An User
	 * @return True if added; false otherwise
	 */
	public boolean addUserInEventWaitingQueue(User u) {
		boolean result;
		if (this.waitingUsers.size()<=this.maxPlayer) {
			result = true;
			this.waitingUsers.put(u.getUUID(), u);
		}
		else {
			result = false;
		}
		return result;
	}
	

	
	/**
	 * Add an User into the event list.
	 * Return true if the User has been added to the event.
	 * If there is not enough places (aka max player count reached), then return false.
	 * @param user An User
	 * @return True if added; false otherwise
	 */
	public boolean addUserInEvent(User user) {
		boolean result;
		if (this.registeredUsers.size()<=this.maxPlayer) {
			result = true;
			this.registeredUsers.put(user.getUUID(), user);
		}
		else {
			result = false;
		}
		return result;
	}
	


	/**
	 * Adding a title to the event : Length max : 45
	 * @param title The title
	 * @throws IllegalArgumentException When the title's length is longer than 45 chars
	 */
	private void setTitle(String title) throws IllegalArgumentException {
		if (title.length()>45)
			throw new IllegalArgumentException("Description must not be over 45 characters.");
		else
			this.title = title;
	}
	
	/**
	 * Adding a description to the event. Length max : 255
	 * @param description The description
	 * @throws IllegalArgumentException When the description's length is longer than 255 chars
	 */
	public void setDescription(String description) throws IllegalArgumentException {
		if (description.length()>255)
			throw new IllegalArgumentException("Description must not be over 255 characters.");
		else
			this.description = description;
	}

	/**
	 * Adding the beginning and ending date of the event
	 * @param start The beginning of the event
	 * @param end The end of the event
	 * @throws IllegalArgumentException When end is before start
	 */
	public void setStartAndEndDate(Date start, Date end) throws IllegalArgumentException {
		if (start.equals(end) || start.before(end)) {
			this.startingDate = start;
			this.endingDate = start;
		}
		else
			throw new IllegalArgumentException("Starting date must be before ending date.");
	}
	
	/**
	 * Adding the beginning the event. The ending date will the same as the beginning date.
	 * @param start The beginning of the event
	 */
	public void setStartDate(Date start) throws IllegalArgumentException {
		this.setStartAndEndDate(start, start);		
	}


	// Other getters and setters
	
	public boolean isPrivate() {
		return this.isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}


	public User getCreator() {
		return this.creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getMinPlayer() {
		return minPlayer;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getStartingDate() {
		return startingDate;
	}

	public Date getEndingDate() {
		return endingDate;
	}

	public UUID getEventUUID() {
		return eventUUID;
	}

}
