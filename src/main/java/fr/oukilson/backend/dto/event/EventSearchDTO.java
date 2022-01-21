package fr.oukilson.backend.dto.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchDTO {
    private String date;
    private String town;
}