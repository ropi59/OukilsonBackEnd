package fr.oukilson.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {

    private Long id;
    private String username;
    private String password;
    private String email;
}
