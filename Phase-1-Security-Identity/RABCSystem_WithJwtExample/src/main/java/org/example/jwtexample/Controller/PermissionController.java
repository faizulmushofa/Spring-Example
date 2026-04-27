package org.example.jwtexample.Controller;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.Permission;
import org.example.jwtexample.Service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_ALL')")
@CrossOrigin("*")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(permissionService.create(permission));
    }

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Permission> getPermissionByCode(@PathVariable String code) {
        return ResponseEntity.ok(permissionService.findByCode(code));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{permissionId}/assign-role/{roleId}")
    public ResponseEntity<Void> assignToRole(@PathVariable Long permissionId, @PathVariable Long roleId) {
        permissionService.assignToRole(permissionId, roleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{permissionId}/unassign-role/{roleId}")
    public ResponseEntity<Void> unassignFromRole(@PathVariable Long permissionId, @PathVariable Long roleId) {
        permissionService.unassignFromRole(permissionId, roleId);
        return ResponseEntity.ok().build();
    }
}
