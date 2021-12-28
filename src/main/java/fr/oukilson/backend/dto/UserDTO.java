package fr.oukilson.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String nickname;
    private List<UserDTO> friendList;
    private List<UserDTO> deniedList;

    public UserDTO(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.friendList = new ArrayList<>();
        this.deniedList = new ArrayList<>();
    }
}
