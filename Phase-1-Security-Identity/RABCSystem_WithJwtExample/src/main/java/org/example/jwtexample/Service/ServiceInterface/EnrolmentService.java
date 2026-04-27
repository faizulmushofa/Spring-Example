package org.example.jwtexample.Service.ServiceInterface;

import org.example.jwtexample.Dto.EnrolmentDto;
import org.example.jwtexample.Model.Enrolment;

import java.util.List;

public interface EnrolmentService {

    Enrolment enroll(Long userId, Long courseId);

    void unenroll(Long userId, Long courseId);

    List<EnrolmentDto> getUserCourses(Long userId);

    List<EnrolmentDto> getCourseUsers(Long courseId);

    List<Enrolment> findAllEnrolment();

    void acceptEnrolment(Long enrolmentId);
    void rejectEnrolment(Long enrolmentId);


}
