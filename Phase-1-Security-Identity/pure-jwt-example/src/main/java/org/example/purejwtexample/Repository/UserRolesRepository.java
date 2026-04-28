package org.example.purejwtexample.Repository;

import org.example.purejwtexample.Model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRole,Long> {
}
