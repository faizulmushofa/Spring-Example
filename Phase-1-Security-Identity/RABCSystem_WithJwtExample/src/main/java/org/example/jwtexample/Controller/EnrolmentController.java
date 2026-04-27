package org.example.jwtexample.Controller;

import lombok.RequiredArgsConstructor;

import org.example.jwtexample.Dto.EnrolmentDto;
import org.example.jwtexample.Model.Enrolment;
import org.example.jwtexample.Service.ServiceInterface.EnrolmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrolments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EnrolmentController {

    private final EnrolmentService enrolmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ENROL_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<Enrolment> enroll(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long courseId = body.get("courseId");
        return ResponseEntity.ok(enrolmentService.enroll(userId, courseId));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ENROL_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<Void> unenroll(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long courseId = body.get("courseId");
        enrolmentService.unenroll(userId, courseId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/courses")
    @PreAuthorize("hasAuthority('ENROL_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<List<EnrolmentDto>> getUserCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(enrolmentService.getUserCourses(userId));
    }

    @GetMapping("/course/{courseId}/users")
    @PreAuthorize("hasAuthority('MAKE_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<List<EnrolmentDto>> getCourseUsers(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrolmentService.getCourseUsers(courseId));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('MAKE_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<Void> approveEnrolment(@PathVariable Long id) {
        enrolmentService.acceptEnrolment(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('MAKE_COURSE') or hasAuthority('MANAGE_ALL')")
    public ResponseEntity<Void> rejectEnrolment(@PathVariable Long id) {
        enrolmentService.rejectEnrolment(id);
        return ResponseEntity.ok().build();
    }
}
