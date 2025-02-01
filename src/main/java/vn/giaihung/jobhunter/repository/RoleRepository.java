package vn.giaihung.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.giaihung.jobhunter.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String name);

    Role findByName(String name);
}
