package org.example.jwtexample.Controller;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.Course;
import org.example.jwtexample.Service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.security.Principal;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAuthority('MAKE_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<Course> createCourse(@RequestBody Course course, Principal principal) {
        return ResponseEntity.ok(courseService.create(course, principal.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_ALL')")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ENROL_COURSE') or hasAuthority('MAKE_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<List<Course>> getAllActiveCourses() {
        return ResponseEntity.ok(courseService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MAKE_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        return ResponseEntity.ok(courseService.update(course));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MAKE_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<Void> deActiveCourse(@PathVariable Long id) {
        courseService.deActive(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lecturer/{lecturerId}")
    public ResponseEntity<List<Course>> getCoursesByLecturerId(@PathVariable Long lecturerId) {
        return ResponseEntity.ok(courseService.getCoursesByLecturerId(lecturerId));
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasAuthority('MAKE_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<List<Course>> getMyCourses(Principal principal) {
        return ResponseEntity.ok(courseService.getMyCourses(principal.getName()));
    }
}
