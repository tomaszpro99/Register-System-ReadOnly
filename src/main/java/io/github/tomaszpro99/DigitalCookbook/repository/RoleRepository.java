package io.github.tomaszpro99.DigitalCookbook.repository;


import io.github.tomaszpro99.DigitalCookbook.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
