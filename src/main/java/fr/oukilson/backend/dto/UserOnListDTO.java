package fr.oukilson.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserOnListDTO {

    private String email;
    private String nickname;


    public UserOnListDTO(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
