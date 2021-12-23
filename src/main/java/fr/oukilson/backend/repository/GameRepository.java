package fr.oukilson.backend.repository;

import fr.oukilson.backend.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
