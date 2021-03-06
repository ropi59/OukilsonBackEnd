package fr.oukilson.backend.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name="location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // DB id
    private String town;
    @Column(name = "zip_code")
    private String zipCode;
    private String address;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "location")
    private Event event;
}
