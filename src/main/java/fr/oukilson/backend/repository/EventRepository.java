package fr.oukilson.backend.repository;

import fr.oukilson.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByUuid(String uuid);
    List<Event> findAllByStartingDateAfter(LocalDateTime date);
    List<Event> findAllByLocationTown(String town);
    void deleteByUuid(String uuid);
}
