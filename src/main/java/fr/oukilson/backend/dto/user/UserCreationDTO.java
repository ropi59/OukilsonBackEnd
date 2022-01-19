package fr.oukilson.backend.dto.user;

import lombok.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {
    @NotNull
    private String password;
    @NotNull
    private String email;
    @NotNull
    private String nickname;
}
