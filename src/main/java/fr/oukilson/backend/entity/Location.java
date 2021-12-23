package fr.oukilson.backend.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

	/**
	 * attributes
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter
	private String location_id;
	private String town;
	private String zip_code;
	private String adress;
	@OneToMany(mappedBy = "location", cascade = {CascadeType.MERGE})
	@Setter
	private List<Event> eventList = new ArrayList<>();

	/**
	 * constructor with town name and zip code
	 * @param town event town name
	 * @param zip_code event town zip code
	 * @throws IllegalArgumentException
	 */
	public Location(String town, String zip_code) throws IllegalArgumentException{
		this.setTown(town);
		this.setZip_code(zip_code);
	}

	//SETTERS
	/**
	 * function to verify if town name has valid length 
	 * @param town
	 * @throws IllegalArgumentException returns exception if input is too long or too short
	 */
	public void setTown(String town) throws IllegalArgumentException{
		if (Tools.checkLength(town, 100)) {
			this.town = town;
        }
	}

	/**
	 * function to verify if zip_code is valid
	 * @param zip_code
	 * @throws IllegalArgumentException returns exception if input isn't in valid zip_code
	 */
	public void setZip_code(String zip_code) throws IllegalArgumentException{
		if (Tools.checkValidString(zip_code, 10,  4))
			this.zip_code = zip_code;
    }

	/**
	 * function to verify if adress is valid
	 * @param adress event number and street
	 * @throws IllegalArgumentException returns exception if input is too long or empty
	 */
	public void setAdress(String adress) throws IllegalArgumentException{
		if (Tools.checkLength(adress, 200)) {
			this.adress = adress;
        }
	}
}
