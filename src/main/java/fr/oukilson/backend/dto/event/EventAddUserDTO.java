package fr.oukilson.backend.dto.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAddUserDTO {
    private String uuid;
    private String nickname;
}
