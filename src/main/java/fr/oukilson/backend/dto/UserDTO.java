package fr.oukilson.backend.dto;

import javax.persistence.Id;
import java.sql.Blob;

public class UserDTO {

    @Id
    private String id;
    private String nickname; //TODO test unicité du nickname
    private Blob icon;
}
