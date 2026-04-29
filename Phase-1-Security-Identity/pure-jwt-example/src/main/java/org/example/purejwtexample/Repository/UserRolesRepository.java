package org.example.purejwtexample.Repository;

import org.example.purejwtexample.Model.Role;
import org.example.purejwtexample.Model.User;
import org.example.purejwtexample.Model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRole,Long> {

    Optional<UserRole> findByUserAndRole(User user, Role role);

    boolean existsByUserAndRole(User user, Role role);
}
