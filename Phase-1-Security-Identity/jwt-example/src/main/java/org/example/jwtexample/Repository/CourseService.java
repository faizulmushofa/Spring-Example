package org.example.jwtexample.Repository;

import org.example.jwtexample.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseService extends JpaRepository<Course,Long> {
}
