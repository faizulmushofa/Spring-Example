package org.example.jwtexample.Service;

import org.example.jwtexample.Dto.EnrolmentDto;
import org.example.jwtexample.Dto.UserDto;
import org.example.jwtexample.Model.Course;
import org.example.jwtexample.Model.Enrolment;
import org.example.jwtexample.Model.User;
import org.example.jwtexample.Model.Enum.Status;
import org.example.jwtexample.Service.ServiceInterface.EnrolmentService;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Repository.CourseRepository;
import org.example.jwtexample.Repository.EnrolmentRepository;
import org.example.jwtexample.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrolmentServiceImp implements EnrolmentService {

    private final EnrolmentRepository enrolmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public Enrolment enroll(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        //check user sudah di course belum 
        Optional<Enrolment> enrolment = enrolmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (enrolment.isPresent()) {
            throw new RuntimeException("User is already enrolled in this course");
        }

        Enrolment newEnrolment = new Enrolment();
        newEnrolment.setUser(user);
        newEnrolment.setCourse(course);
        newEnrolment.setStatus(Status.PENDING);
        
        return enrolmentRepository.save(newEnrolment);
        
    }



    

    @Override
    public void unenroll(Long userId, Long courseId) {
        Enrolment enrolment = enrolmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrolment not found"));
        enrolmentRepository.delete(enrolment);
    }

    @Override
    public List<EnrolmentDto> getUserCourses(Long userId) {
        return enrolmentRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<EnrolmentDto> getCourseUsers(Long courseId) {
        return enrolmentRepository.findByCourseId(courseId).stream()
                .map(this::mapToDto)
                .toList();
    }

    private EnrolmentDto mapToDto(Enrolment e) {
            UserDto userDto = new org.example.jwtexample.Dto.UserDto(e.getUser().getId(), e.getUser().getEmail());
        return new EnrolmentDto(e.getId(), userDto, e.getCourse(), e.getStatus(), e.getEnrollAt());
    }

    @Override
    public List<Enrolment> findAllEnrolment() {
        return enrolmentRepository.findAll();
    }

    @Override
    public void acceptEnrolment(Long enrolmentId) {
        Enrolment enrolment = enrolmentRepository.findById(enrolmentId)
                .orElseThrow(() -> new RuntimeException("Enrolment not found"));
        enrolment.setStatus(Status.APPROVED);
        enrolmentRepository.save(enrolment);
    }


    @Override
    public void rejectEnrolment(Long enrolmentId) {
        Enrolment enrolment = enrolmentRepository.findById(enrolmentId)
                .orElseThrow(() -> new RuntimeException("Enrolment not found"));
        enrolment.setStatus(Status.REJECTED);
        enrolmentRepository.save(enrolment);
    }
}
