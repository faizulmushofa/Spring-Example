package org.example.jwtexample.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "permission_id",nullable = false)
    private Permission permission;

}
