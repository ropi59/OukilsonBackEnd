package fr.oukilson.backend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@location_id")
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
		if (Tools.checkLength(town, 45)) {
			this.town = town;
        }
	}

	/**
	 * function to verify if zip_code is valid
	 * @param zip_code
	 * @throws IllegalArgumentException returns exception if input isn't in valid zip_code
	 */
	public void setZip_code(String zip_code) throws IllegalArgumentException{
		if (Tools.checkLength(zip_code, 5))
			this.zip_code = zip_code;
    }

	/**
	 * function to verify if adress is valid
	 * @param adress event number and street
	 * @throws IllegalArgumentException returns exception if input is too long or empty
	 */
	public void setAdress(String adress) throws IllegalArgumentException{
		//TODO modify adress length in db up to 150 char (45 too short to number, street type and street name)
		if (Tools.checkLength(adress, 150)) {
			this.adress = adress;
        }
	}
}
