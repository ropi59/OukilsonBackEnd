package fr.oukilson.backend.dto.user;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {
    @NotBlank
    @NotNull
    private String nickname;
    @NotBlank
    @NotNull
    private String password;
    @NotBlank
    @NotNull
    private String email;

    /**
     * Check if this DTO has valid values, meaning :
     * - nickname is not null and match the provided regex pattern (nicknamePattern)
     * - email is not null and match the provided regex pattern (emailPattern)
     * - password is not null and not blank
     * @param nicknamePattern RegEx Pattern for the nickname
     * @param emailPattern RegEx Pattern for the email
     * @return True if the DTO is valid
     */
    public boolean isValid(Pattern nicknamePattern, Pattern emailPattern) {
        return nickname!=null && password!=null && email!=null && !password.isBlank()
                && nicknamePattern.matcher(nickname).find() && emailPattern.matcher(email).find();
    }
}
