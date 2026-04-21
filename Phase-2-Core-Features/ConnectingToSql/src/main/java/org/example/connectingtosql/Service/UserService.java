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
public class UserService {

    private final UserRepository userRepository;

    // Ambil semua user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Ambil user berdasarkan id
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Tambah user baru
    public Map<String, Object> createUser(User user) {
        Map<String, Object> response = new HashMap<>();
        user.setIsActive(true);
        int result = userRepository.save(user);
        response.put("success", result > 0);
        response.put("message", result > 0 ? "User berhasil ditambahkan." : "Gagal menambahkan user.");
        return response;
    }

    // Update data user
    public Map<String, Object> updateUser(User user) {
        Map<String, Object> response = new HashMap<>();
        int result = userRepository.update(user);
        response.put("success", result > 0);
        response.put("message", result > 0 ? "User berhasil diupdate." : "Gagal mengupdate user.");
        return response;
    }

    // Soft delete user (nonaktifkan)
    public Map<String, Object> deactivateUser(Long id) {
        Map<String, Object> response = new HashMap<>();
        int result = userRepository.softDelete(id);
        response.put("success", result > 0);
        response.put("message", result > 0 ? "User berhasil dinonaktifkan." : "Gagal menonaktifkan user.");
        return response;
    }
}
