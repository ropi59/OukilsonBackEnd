package fr.oukilson.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Icon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String IconUrl;
    private String IconType;
    private Long IconSize;
}