package org.example.purejwtexample.Service;

import lombok.RequiredArgsConstructor;
import org.example.purejwtexample.Dtos.AssignRoleRequest;
import org.example.purejwtexample.Dtos.RegisterRequest;
import org.example.purejwtexample.Dtos.UserResponse;
import org.example.purejwtexample.Model.Role;
import org.example.purejwtexample.Model.User;
import org.example.purejwtexample.Model.UserRole;
import org.example.purejwtexample.Repository.RoleRepository;
import org.example.purejwtexample.Repository.UserRepository;
import org.example.purejwtexample.Repository.UserRolesRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRolesRepository userRolesRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email sudah digunakan");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan dengan id: " + id));
        return toResponse(user);
    }


    @Transactional
    public UserResponse update(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan dengan id: " + id));


        if (!user.getUsername().equals(request.username())
                && userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }

        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email sudah digunakan");
        }

        user.setUsername(request.username());
        user.setEmail(request.email());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User tidak ditemukan dengan id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse assignRole(Long userId, AssignRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan dengan id: " + userId));

        Role role = roleRepository.findByName(request.roleName())
                .orElseThrow(() -> new IllegalArgumentException("Role tidak ditemukan: " + request.roleName()));

        if (userRolesRepository.existsByUserAndRole(user, role)) {
            throw new IllegalArgumentException("User sudah memiliki role: " + request.roleName());
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRolesRepository.save(userRole);

        User refreshed = userRepository.findById(userId).orElseThrow();
        return toResponse(refreshed);
    }

   
    @Transactional
    public UserResponse removeRole(Long userId, AssignRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan dengan id: " + userId));

        Role role = roleRepository.findByName(request.roleName())
                .orElseThrow(() -> new IllegalArgumentException("Role tidak ditemukan: " + request.roleName()));

        UserRole userRole = userRolesRepository.findByUserAndRole(user, role)
                .orElseThrow(() -> new IllegalArgumentException("User tidak memiliki role: " + request.roleName()));

        userRolesRepository.delete(userRole);

        User refreshed = userRepository.findById(userId).orElseThrow();
        return toResponse(refreshed);
    }


    private UserResponse toResponse(User user) {
        List<String> roles = user.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }
}
