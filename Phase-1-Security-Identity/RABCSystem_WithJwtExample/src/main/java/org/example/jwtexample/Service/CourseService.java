package org.example.jwtexample.Service;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.Course;
import org.example.jwtexample.Repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import org.example.jwtexample.Repository.UserRepository;
import org.example.jwtexample.Model.User;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public Course create(Course course, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        course.setUser(user);
        return courseRepository.save(course);
    }

    public void deActive(Long courseId){
        Course course = getById(courseId);
        course.setActive(false);
        courseRepository.save(course);
    }

    public Course getById(Long Id){
        return courseRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + Id));
    }

    public List<Course> getAll(){
        return courseRepository.findAll();
    }

    public List<Course> getAllActive(){
        return courseRepository.findAll().stream().filter(c -> c.isActive() == true).toList();
    }

    public Course update(Course course){
        Course existing = courseRepository.findById(course.getId())
                .orElseThrow(() -> new RuntimeException("Course not found for update"));
        existing.setTittle(course.getTittle());
        existing.setDescription(course.getDescription());
        existing.setActive(course.isActive());
        return courseRepository.save(existing);
    }

    public List<Course> getCoursesByLecturerId(Long lecturerId) {
        return courseRepository.findByUserId(lecturerId);
    }

    public List<Course> getMyCourses(String email) {
        return courseRepository.findByUserEmail(email);
    }

}
