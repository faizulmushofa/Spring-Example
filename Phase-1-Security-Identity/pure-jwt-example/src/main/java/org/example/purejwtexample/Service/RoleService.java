package org.example.purejwtexample.Service;

import lombok.RequiredArgsConstructor;
import org.example.purejwtexample.Dtos.RoleRequest;
import org.example.purejwtexample.Dtos.RoleResponse;
import org.example.purejwtexample.Model.Role;
import org.example.purejwtexample.Repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Role sudah ada: " + request.name());
        }

        Role role = new Role();
        role.setName(request.name());

        Role saved = roleRepository.save(role);
        return toResponse(saved);
    }

    public List<RoleResponse> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RoleResponse findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role tidak ditemukan dengan id: " + id));
        return toResponse(role);
    }

    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role tidak ditemukan dengan id: " + id));

        if (!role.getName().equals(request.name())
                && roleRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Role sudah ada: " + request.name());
        }

        role.setName(request.name());
        Role updated = roleRepository.save(role);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Role tidak ditemukan dengan id: " + id);
        }
        roleRepository.deleteById(id);
    }


    private RoleResponse toResponse(Role role) {
        return new RoleResponse(role.getId(), role.getName());
    }
}
