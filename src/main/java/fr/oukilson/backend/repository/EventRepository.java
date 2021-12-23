package fr.oukilson.backend.repository;

import fr.oukilson.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByUuid(String uuid);
    void deleteByUuid(String uuid);
}
