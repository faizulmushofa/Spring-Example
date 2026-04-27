package org.example.jwtexample.Repository;

import org.example.jwtexample.Model.Enrolment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment,Long> {
    Optional<Enrolment> findByUserIdAndCourseId(Long userId, Long courseId);
    List<Enrolment> findByUserId(Long userId);
    List<Enrolment> findByCourseId(Long courseId);
}
