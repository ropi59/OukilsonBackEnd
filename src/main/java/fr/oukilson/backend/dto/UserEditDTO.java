package fr.oukilson.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDTO {

    private String firstName;
    private String lastName;
    private String email; //TODO test unicité du mail
    private String nickname; //TODO test unicité du nickname
    private String iconFilename;
}
