package com.example.faizul.Config;

import com.example.faizul.Model.Role;
import com.example.faizul.Model.StoredFile;
import com.example.faizul.Model.User;
import com.example.faizul.Repository.FileRepository;
import com.example.faizul.Repository.RoleRepository;
import com.example.faizul.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Buat Roles
        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN", "Administrator Role")));
        
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER", "User Role")));

        // 2. Buat User 1: Admin & User
        if (userRepository.findByUserEmail("admin@gmail.com").isEmpty()) {
            User admin = new User(
                    null,
                    "Admin Faizul",
                    "admin@gmail.com",
                    passwordEncoder.encode("admin123"),
                    true,
                    50000L,
                    Set.of(adminRole, userRole)
            );
            admin = userRepository.save(admin);

            // Dummy Files untuk Admin
            fileRepository.save(new StoredFile(null, "hash_admin_1", "laporan_tahunan.pdf", "/data/admin", false, 1024L, admin));
            fileRepository.save(new StoredFile(null, "hash_admin_2", "foto_profil.jpg", "/data/admin", false, 2048L, admin));
        }

        // 3. Buat User 2: User Only
        if (userRepository.findByUserEmail("user@gmail.com").isEmpty()) {
            User normalUser = new User(
                    null,
                    "User Biasa",
                    "user@gmail.com",
                    passwordEncoder.encode("user123"),
                    true,
                    10000L,
                    Set.of(userRole)
            );
            normalUser = userRepository.save(normalUser);

            // Dummy Files untuk User
            fileRepository.save(new StoredFile(null, "hash_user_1", "tugas_kuliah.docx", "/data/user", false, 512L, normalUser));
        }

        System.out.println(">>> Dummy Data Seeded Successfully!");
    }
}
