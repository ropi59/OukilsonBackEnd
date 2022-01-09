package fr.oukilson.backend.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateLocationDTO {
    private String town;
    private String zipCode;
    private String address;
}
