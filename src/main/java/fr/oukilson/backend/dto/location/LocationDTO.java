package fr.oukilson.backend.dto.location;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private String town;
    private String zipCode;
    private String address;
}
