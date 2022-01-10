package fr.oukilson.backend.repository;

import fr.oukilson.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository <Event, Long> {

}
