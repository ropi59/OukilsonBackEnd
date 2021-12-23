package fr.oukilson.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDTO {

    private String password;
    private String firstName;
    private String lastName;
    private String email; //TODO test unicité du mail
    private String nickname; //TODO test unicité du nickname
}
