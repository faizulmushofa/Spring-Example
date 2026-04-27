package org.example.jwtexample.Repository;

import org.example.jwtexample.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {
    List<Course> findByUserId(Long userId);
    List<Course> findByUserEmail(String email);
}
