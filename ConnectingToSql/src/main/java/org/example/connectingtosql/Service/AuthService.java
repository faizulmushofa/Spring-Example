package org.example.connectingtosql.Service;

import lombok.RequiredArgsConstructor;
import org.example.connectingtosql.Model.User;
import org.example.connectingtosql.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    // Registrasi user baru
    public Map<String, Object> register(User user) {
        Map<String, Object> response = new HashMap<>();

        // Cek duplikasi username
        List<User> existing = userRepository.findAll();
        boolean usernameExists = existing.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));

        if (usernameExists) {
            response.put("success", false);
            response.put("message", "Username '" + user.getUsername() + "' sudah digunakan.");
            return response;
        }

        user.setIsActive(true);
        int result = userRepository.save(user);

        if (result > 0) {
            response.put("success", true);
            response.put("message", "Registrasi berhasil!");
        } else {
            response.put("success", false);
            response.put("message", "Registrasi gagal, coba lagi.");
        }
        return response;
    }

    // Login user
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> response = new HashMap<>();

        List<User> allUsers = userRepository.findAll();
        User user = allUsers.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username)
                        && u.getPassword().equals(password)
                        && Boolean.TRUE.equals(u.getIsActive()))
                .findFirst()
                .orElse(null);

        if (user != null) {
            response.put("success", true);
            response.put("message", "Login berhasil!");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
        } else {
            response.put("success", false);
            response.put("message", "Username atau password salah / akun tidak aktif.");
        }
        return response;
    }
}
