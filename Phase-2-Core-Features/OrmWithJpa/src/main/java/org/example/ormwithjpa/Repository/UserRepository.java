package org.example.ormwithjpa.Repository;

import org.example.ormwithjpa.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String Email);
}
