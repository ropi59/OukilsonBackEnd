package fr.oukilson.backend.entity;

import javax.persistence.*;

/**
 * A server-sided representation of a game event
 */
@Entity
@Table(name = "event")
public class Event {

	@Id
	private Long id;
	@ManyToOne
	@JoinColumn (name = "location_id")
	private Location location;

}
