package org.example.ormwithjpa.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StoredFile {

    @Id
    @Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,
            name = "original_name",
            unique = true
    )
    private String originalName;

    @Column(
            name = "hash_name",
            unique = true
    )
    private String hashName;

    @Column(
            name = "path_file"
    )
    private String path;

    @Column(
            name = "size_file"
    )
    private Long size;

    @CreationTimestamp
    @Column(name = "creadted_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "removed")
    private boolean isRemoved;


    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private User user;

}
