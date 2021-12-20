package fr.oukilson.backend.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {

    private String password;
    private String email;
    private String nickname;



}
