package fr.oukilson.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

    private boolean success;
    private String message;
}
