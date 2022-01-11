package fr.oukilson.backend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRemoveUserDTO {
    private String uuid;
    private String nickname;
}
