package fr.oukilson.backend.dto.event;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchDTO {
    private LocalDateTime startingDate;
    private String town;
}