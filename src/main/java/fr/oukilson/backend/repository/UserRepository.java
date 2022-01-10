package fr.oukilson.backend.repository;

import fr.oukilson.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByNickname(String nickname);
}
