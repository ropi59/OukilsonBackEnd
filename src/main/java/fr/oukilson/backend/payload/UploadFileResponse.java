package fr.oukilson.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponse {

    private String fileName;
    private String IconUri;
    private String IconType;
    private Long IconSize;
}
