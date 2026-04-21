package org.example.ormwithjpa.Service;

import lombok.AllArgsConstructor;
import org.example.ormwithjpa.Dto.request.LoginRequest;
import org.example.ormwithjpa.Dto.request.RegisterRequest;
import org.example.ormwithjpa.Dto.response.LoginResponse;
import org.example.ormwithjpa.Dto.response.RegisterResponse;
import org.example.ormwithjpa.Model.GeneratedPassword;
import org.example.ormwithjpa.Model.User;
import org.example.ormwithjpa.Repository.UserRepository;
import org.example.ormwithjpa.Security.SessionStore;
import org.example.ormwithjpa.Service.Interface.AuthService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.email());

        if (user == null) {
            throw new RuntimeException("User tidak ditemukan!");
        }

        if (GeneratedPassword.matches(loginRequest.password(),user.getPassword())) {
            throw new RuntimeException("Password salah!");
        }

        String sessionId = SessionStore.generateSession();
        SessionStore.save(sessionId, user.getId());

        // kembalikan token (sessionId) sebagai String, bukan Cookie object
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                sessionId
        );
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        // cek apakah email sudah terdaftar
        User existingUser = userRepository.findByEmail(request.email());
        if (existingUser != null) {
            throw new RuntimeException("Email sudah terdaftar!");
        }

        // buat dan simpan user baru
        User newUser = new User();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setPassword(GeneratedPassword.encode(request.password()));

        userRepository.save(newUser);

        return new RegisterResponse("User berhasil didaftarkan", newUser.getUsername());
    }
}
