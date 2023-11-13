package io.github.tomaszpro99.DigitalCookbook.repository;


import io.github.tomaszpro99.DigitalCookbook.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByEmailIgnoreCase(String email);
    Boolean existsByEmail(String email);
}
