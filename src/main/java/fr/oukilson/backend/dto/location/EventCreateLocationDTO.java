package fr.oukilson.backend.dto.location;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateLocationDTO {
    private String town;
    private String zipCode;
    private String address;
}
