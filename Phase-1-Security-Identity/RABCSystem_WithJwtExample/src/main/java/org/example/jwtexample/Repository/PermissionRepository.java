package org.example.jwtexample.Repository;

import org.example.jwtexample.Model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {
    Optional<Permission> findByCode(String code);
}
