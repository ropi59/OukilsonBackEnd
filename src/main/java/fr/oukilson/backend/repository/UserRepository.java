package fr.oukilson.backend.repository;

import fr.oukilson.backend.dto.UserDTO;
import fr.oukilson.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

}
