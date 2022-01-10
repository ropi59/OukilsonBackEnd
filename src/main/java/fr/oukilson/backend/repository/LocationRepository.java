package fr.oukilson.backend.repository;

import fr.oukilson.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByTownAndZipCodeAndAddress(String town, String zipCode, String address);
}
