package fr.oukilson.backend.repository;

import fr.oukilson.backend.dtos.GameUuidDTO;
import fr.oukilson.backend.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByUuid(String uuid);
    List<Game> findAllByNameContaining(String name);
}
