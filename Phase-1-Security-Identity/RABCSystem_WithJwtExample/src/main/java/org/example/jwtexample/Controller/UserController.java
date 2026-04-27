package org.example.jwtexample.Controller;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Dto.UserDto;
import org.example.jwtexample.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_ALL')")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<org.example.jwtexample.Dto.AdminUserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsersForAdmin());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activeUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactiveUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/lecturers")
    public ResponseEntity<List<UserDto>> getAllLecturer() {
        return ResponseEntity.ok(userService.findAllLecturer());
    }

    @GetMapping("/students")
    public ResponseEntity<List<UserDto>> getAllStudent() {
        return ResponseEntity.ok(userService.findAllStudent());
    }


}
