package com.example.faizul.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.faizul.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Boolean existsByUserName(String userName);
    Boolean existsByUserEmail(String userEmail);
    Optional<User> findByUserEmail(String userEmail);
}
