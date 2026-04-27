package org.example.jwtexample.Controller;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.Role;
import org.example.jwtexample.Service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_ALL')")
@CrossOrigin("*")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PostMapping("/{roleId}/assign-user/{userId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long userId, @PathVariable String roleName) {
        roleService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set-dosen/{userId}")
    public ResponseEntity<Void> setAsDosen(@PathVariable Long userId) {
        roleService.setAsDosen(userId);
        return ResponseEntity.ok().build();
    }
}
