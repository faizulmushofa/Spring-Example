package org.example.jwtexample.Repository;

import org.example.jwtexample.Model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
}
