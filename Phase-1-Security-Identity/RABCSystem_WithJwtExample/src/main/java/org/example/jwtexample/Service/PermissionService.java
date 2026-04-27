package org.example.jwtexample.Service;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.Permission;
import org.example.jwtexample.Repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.example.jwtexample.Model.Role;
import org.example.jwtexample.Model.RolePermission;
import org.example.jwtexample.Repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public Permission create(Permission permission){
        return permissionRepository.save(permission);
    }

    public List<Permission> findAll(){
        return permissionRepository.findAll();
    }

    public Permission findByCode(String code){
        return permissionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Permission not found with code: " + code));
    }

    public void delete(Long id){
        permissionRepository.deleteById(id);
    }


    @Transactional
    public void assignToRole(Long permissionId, Long roleId){
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        boolean alreadyAssigned = role.getRolePermissions().stream()
                .anyMatch(rp -> rp.getPermission().getId().equals(permissionId));

        if (!alreadyAssigned) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermission.setCode(permission.getCode()); // copy permission code

            role.getRolePermissions().add(rolePermission);
            roleRepository.save(role);
        }
    }

    @Transactional
    public void unassignFromRole(Long permissionId, Long roleId){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        boolean removed = role.getRolePermissions().removeIf(rp -> rp.getPermission().getId().equals(permissionId));
        
        if (removed) {
            roleRepository.save(role);
        }
    }
}
