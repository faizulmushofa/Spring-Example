package com.example.faizul.Service;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.faizul.Security.Dto.LoginRequest;
import com.example.faizul.Security.Dto.LoginResponse;
import com.example.faizul.Security.Dto.RegisterRequest;
import com.example.faizul.Security.Dto.RegisterResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.faizul.Model.Role;
import com.example.faizul.Model.User;
import com.example.faizul.Model.UserDetailsImp;
import com.example.faizul.Repository.RoleRepository;
import com.example.faizul.Repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public RegisterResponse register(RegisterRequest request){
    
        Set<Role> roles = request.roles().stream()
                .map(roleName -> roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " is not found.")))
                .collect(Collectors.toSet());

        // Jika roles kosong dari request, beri default ROLE_USER
        if (roles.isEmpty()) {
            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role ROLE_USER is not found."));
            roles.add(userRole);
        }

        User newUser = new User(
                null,
                request.userName(),
                request.userEmail(),
                passwordEncoder.encode(request.userPassword()),
                true,
                10000L,
                roles
        );

        userRepository.save(newUser);
        return new RegisterResponse("User registered successfully", true);
    }

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AuthenticationException {
        // Proses verifikasi kredensial (Email dan Password)
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
        );

        // Memasukan authentication ke context holder
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // SIMPAN KE SESSION (Penting untuk Spring Security 6)
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        UserDetailsImp user = (UserDetailsImp) authentication.getPrincipal();
        
    
        return new LoginResponse(
            user.getUserId(),
            user.getUsername(),
            user.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toSet())
        );

    }   

}
