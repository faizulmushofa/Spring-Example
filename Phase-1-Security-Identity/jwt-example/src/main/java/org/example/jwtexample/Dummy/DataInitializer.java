package org.example.jwtexample.Dummy;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.*;
import org.example.jwtexample.Model.Enum.Status;
import org.example.jwtexample.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds Roles, Permissions, Users, Courses, and Enrolments on startup.
 * Idempotent — safe to run multiple times.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrolmentRepository enrolmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // ===== 1. Seed Roles =====
        Role adminRole   = findOrCreateRole("admin");
        Role dosenRole   = findOrCreateRole("dosen");
        Role studentRole = findOrCreateRole("student");

        // ===== 2. Seed Permissions =====
        Permission manageAll   = findOrCreatePermission("MANAGE_ALL");
        Permission makeCourse  = findOrCreatePermission("MAKE_COURSE");
        Permission enrolCourse = findOrCreatePermission("ENROL_COURSE");

        // ===== 3. Bind permissions to roles =====
        assignPermissionToRole(adminRole, manageAll);
        assignPermissionToRole(dosenRole, makeCourse);
        assignPermissionToRole(studentRole, enrolCourse);

        // ===== 4. Seed Users =====
        User admin = findOrCreateUser("admin@lms.com", "admin123", adminRole);
        System.out.println(">>> Admin  : admin@lms.com / admin123");

        User dosen1 = findOrCreateUser("budi.dosen@lms.com", "dosen123", dosenRole);
        User dosen2 = findOrCreateUser("siti.dosen@lms.com", "dosen123", dosenRole);
        User dosen3 = findOrCreateUser("andi.dosen@lms.com", "dosen123", dosenRole);
        System.out.println(">>> Dosen  : budi.dosen@lms.com, siti.dosen@lms.com, andi.dosen@lms.com / dosen123");

        User student1 = findOrCreateUser("raka@student.com", "student123", studentRole);
        User student2 = findOrCreateUser("dewi@student.com", "student123", studentRole);
        User student3 = findOrCreateUser("fajar@student.com", "student123", studentRole);
        User student4 = findOrCreateUser("nina@student.com", "student123", studentRole);
        User student5 = findOrCreateUser("rizky@student.com", "student123", studentRole);
        System.out.println(">>> Student: raka, dewi, fajar, nina, rizky @student.com / student123");

        // ===== 5. Seed Courses =====
        Course c1 = findOrCreateCourse("Pemrograman Java Dasar",
                "Belajar fundamental bahasa Java mulai dari syntax, OOP, hingga Collections.", dosen1);
        Course c2 = findOrCreateCourse("Basis Data Relasional",
                "Memahami desain ERD, normalisasi, SQL query, dan optimasi database.", dosen1);
        Course c3 = findOrCreateCourse("Algoritma & Struktur Data",
                "Menguasai sorting, searching, linked list, tree, dan graph.", dosen2);
        Course c4 = findOrCreateCourse("Pengembangan Web Modern",
                "Fullstack web development dengan HTML, CSS, JavaScript, dan Spring Boot.", dosen2);
        Course c5 = findOrCreateCourse("Kecerdasan Buatan",
                "Pengantar AI, machine learning, neural network, dan NLP.", dosen3);
        Course c6 = findOrCreateCourse("Keamanan Sistem Informasi",
                "Kriptografi, autentikasi, otorisasi, dan penetration testing.", dosen3);

        // ===== 6. Seed Enrolments (simulasi) =====
        // Raka & Dewi di course Java (APPROVED)
        findOrCreateEnrolment(student1, c1, Status.APPROVED);
        findOrCreateEnrolment(student2, c1, Status.APPROVED);

        // Fajar di course Java (PENDING - belum disetujui)
        findOrCreateEnrolment(student3, c1, Status.PENDING);

        // Raka juga ikut Basis Data (APPROVED)
        findOrCreateEnrolment(student1, c2, Status.APPROVED);

        // Dewi ikut Algoritma (APPROVED), Nina (PENDING)
        findOrCreateEnrolment(student2, c3, Status.APPROVED);
        findOrCreateEnrolment(student4, c3, Status.PENDING);

        // Fajar & Rizky ikut Web Modern (APPROVED)
        findOrCreateEnrolment(student3, c4, Status.APPROVED);
        findOrCreateEnrolment(student5, c4, Status.APPROVED);

        // Nina ikut AI (PENDING), Rizky (REJECTED)
        findOrCreateEnrolment(student4, c5, Status.PENDING);
        findOrCreateEnrolment(student5, c5, Status.REJECTED);

        // Raka ikut Keamanan (APPROVED)
        findOrCreateEnrolment(student1, c6, Status.APPROVED);

        System.out.println(">>> Seed data complete! 6 courses, 11 enrolments.");
    }

    // ===== Helper Methods =====

    private Role findOrCreateRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role r = new Role();
            r.setName(name);
            return roleRepository.save(r);
        });
    }

    private Permission findOrCreatePermission(String code) {
        return permissionRepository.findByCode(code).orElseGet(() -> {
            Permission p = new Permission();
            p.setCode(code);
            return permissionRepository.save(p);
        });
    }

    private void assignPermissionToRole(Role role, Permission permission) {
        boolean exists = role.getRolePermissions().stream()
                .anyMatch(rp -> rp.getPermission().getId().equals(permission.getId()));
        if (!exists) {
            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setPermission(permission);
            rp.setCode(permission.getCode());
            role.getRolePermissions().add(rp);
            roleRepository.save(role);
        }
    }

    private User findOrCreateUser(String email, String password, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            u.setActive(true);
            u.addRole(role);
            return userRepository.save(u);
        });
    }

    private Course findOrCreateCourse(String tittle, String description, User dosen) {
        return courseRepository.findByUserEmail(dosen.getEmail()).stream()
                .filter(c -> c.getTittle().equals(tittle))
                .findFirst()
                .orElseGet(() -> {
                    Course c = new Course();
                    c.setTittle(tittle);
                    c.setDescription(description);
                    c.setActive(true);
                    c.setUser(dosen);
                    return courseRepository.save(c);
                });
    }

    private void findOrCreateEnrolment(User student, Course course, Status status) {
        boolean exists = enrolmentRepository.findByUserIdAndCourseId(student.getId(), course.getId()).isPresent();
        if (!exists) {
            Enrolment e = new Enrolment();
            e.setUser(student);
            e.setCourse(course);
            e.setStatus(status);
            enrolmentRepository.save(e);
        }
    }
}
