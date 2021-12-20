package fr.oukilson.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreationResponseDTO {

    private boolean success;
    private String message;
}
