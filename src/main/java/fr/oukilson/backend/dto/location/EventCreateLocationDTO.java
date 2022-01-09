package fr.oukilson.backend.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateLocationDTO {
    private String town;
    private String zipCode;
    private String address;
}
