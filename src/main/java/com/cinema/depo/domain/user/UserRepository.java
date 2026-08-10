package com.cinema.depo.domain.user;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email); // Spring génère le SQL tout seul à partir du nom de méthode
}