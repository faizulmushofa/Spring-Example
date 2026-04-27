package org.example.jwtexample.Service;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.User;
import org.example.jwtexample.Repository.UserRepository;
import org.example.jwtexample.Repository.RoleRepository;
import org.example.jwtexample.Model.Role;
import org.example.jwtexample.Service.ServiceInterface.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.jwtexample.Dto.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                () -> new UsernameNotFoundException("User Email Not Found")
        );

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())){
            throw new BadCredentialsException("Password Didnt matches");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, new UserDto(user.getId(), user.getEmail()));
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (registerRequest == null){
            throw new RuntimeException("Input didnt valid");
        }
        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setActive(true); 
        
        Role studentRole = roleRepository.findByName("student").orElseThrow(
                () -> new RuntimeException("Role 'student' not found")
        );
        user.addRole(studentRole);
        
        User savedUser = userRepository.save(user);
        return new RegisterResponse("User registered successfully", new UserDto(savedUser.getId(), savedUser.getEmail()));
    }
}
