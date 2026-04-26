package org.example.jwtexample.Repository;

import org.example.jwtexample.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
