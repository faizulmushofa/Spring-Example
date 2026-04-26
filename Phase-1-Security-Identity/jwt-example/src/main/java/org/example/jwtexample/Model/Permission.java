package org.example.jwtexample.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@Table(name = "permissions")
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @OneToMany(mappedBy = "permission",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private List<RolePermission> rolePermissions = new ArrayList<>();
}
