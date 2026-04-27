package org.example.jwtexample.Service;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.Role;
import org.example.jwtexample.Repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.transaction.Transactional;
import org.example.jwtexample.Model.User;
import org.example.jwtexample.Repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public Role createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role '" + name + "' not found"));
    }

    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Role role = findByName(roleName);
        user.addRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void setAsDosen(Long userId) {
        assignRoleToUser(userId, "dosen");
    }
}
