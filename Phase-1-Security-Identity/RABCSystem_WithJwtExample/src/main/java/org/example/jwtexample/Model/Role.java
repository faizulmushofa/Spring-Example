package org.example.jwtexample.Model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "roles")
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "role",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<UserRole> userRoles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "role",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<RolePermission> rolePermissions = new ArrayList<>();


}
