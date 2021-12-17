package fr.oukilson.backend.dto;


import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.util.HashMap;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {

    private Long id;
    private String password;
    private String email;
    private String nickname;

}
