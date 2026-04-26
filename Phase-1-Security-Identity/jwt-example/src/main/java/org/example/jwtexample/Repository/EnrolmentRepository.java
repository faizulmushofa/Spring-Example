package org.example.jwtexample.Repository;

import org.example.jwtexample.Model.Enrolment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrolmentRepository extends JpaRepository<Enrolment,Long> {
}
