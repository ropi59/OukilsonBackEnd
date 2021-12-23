package fr.oukilson.backend.repository;

import fr.oukilson.backend.entity.Icon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IconRepository extends JpaRepository <Icon, String> {
}
