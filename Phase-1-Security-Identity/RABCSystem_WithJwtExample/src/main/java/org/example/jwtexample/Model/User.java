package org.example.jwtexample.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    private String email;

    @JsonIgnore
    private String password;

    private boolean isActive;

    @JsonIgnore
    @OneToMany(mappedBy = "user",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<UserRole> userRoles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Enrolment> enrolments;


    public void addRole(Role role){
        UserRole newUserRole = new UserRole();
        newUserRole.setUser(this);
        newUserRole.setRole(role);
        this.userRoles.add(newUserRole);

    }

}
