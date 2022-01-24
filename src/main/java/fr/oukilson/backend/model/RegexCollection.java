package fr.oukilson.backend.model;

import lombok.*;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegexCollection {
    private Pattern emailPattern;
    private Pattern nicknamePattern;
    private Pattern namePattern;
}