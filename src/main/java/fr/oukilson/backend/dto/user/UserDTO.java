package fr.oukilson.backend.dto.user;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String nickname;
    private List<UserNameDTO> friendList;
}
