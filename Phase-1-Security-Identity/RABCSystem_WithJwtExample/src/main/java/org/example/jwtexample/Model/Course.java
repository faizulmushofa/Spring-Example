package org.example.jwtexample.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tittle;

    private String description;

    private boolean isActive;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Enrolment> enrolments;
}
