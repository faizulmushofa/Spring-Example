package org.example.jwtexample.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.example.jwtexample.Dto.AdminUserDto;
import org.example.jwtexample.Dto.UserDto;
import org.example.jwtexample.Model.User;
import org.example.jwtexample.Repository.CourseRepository;
import org.example.jwtexample.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    
    @Transactional
    public void save(User user){
        userRepository.save(user);
    }

    public List<AdminUserDto> findAllUsersForAdmin() {
        return userRepository.findAll().stream().map(user -> {
            List<String> roles = user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getName())
                    .toList();
            List<String> permissions = user.getUserRoles().stream()
                    .flatMap(ur -> ur.getRole().getRolePermissions().stream())
                    .map(rp -> rp.getPermission().getCode())
                    .distinct()
                    .toList();
            int courseCount = 0;
            if (roles.contains("dosen")) {
                courseCount = courseRepository.findByUserEmail(user.getEmail()).size();
            } else if (roles.contains("student")) {
                courseCount = user.getEnrolments() != null ? user.getEnrolments().size() : 0;
            }
            return new AdminUserDto(user.getId(), user.getEmail(), user.isActive(), roles, permissions, courseCount);
        }).toList();
    }

    public UserDto findUserById(Long id){
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<UserDto> findAllUser(){
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public UserDto findByEmail(String email){
        return userRepository.findByEmail(email)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    //mapping sederhana lebih di sarankan menggunakan "@Mapper" jika di production
    private UserDto mapToDto(User user) {
        return new UserDto(user.getId(), user.getEmail());
    }

    public void activeUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setActive(true);
        userRepository.save(user);
    }
    public void deactiveUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    public List<UserDto> findAllLecturer(){
        return userRepository.findAll().stream()
                .filter(user -> user.getUserRoles().stream().anyMatch(
                    role -> role.getRole().getName().equals("dosen")))
                    .filter(user -> user.isActive())
                .map(this::mapToDto)
                .toList();
    }
    public List<UserDto> findAllStudent(){
        return userRepository.findAll().stream()
                .filter(user -> user.getUserRoles().stream().anyMatch(
                    role -> role.getRole().getName().equals("student")))
                    .filter(user -> user.isActive())
                .map(this::mapToDto)
                .toList();
    }
}
