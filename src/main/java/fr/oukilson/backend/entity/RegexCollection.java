package fr.oukilson.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegexCollection {

    private Pattern emailPattern;
    private Pattern nicknamePattern;
    private Pattern namePattern;
}
