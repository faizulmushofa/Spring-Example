package org.example.jwtexample.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

    @CreationTimestamp
    private LocalDateTime assignedAt;
}
