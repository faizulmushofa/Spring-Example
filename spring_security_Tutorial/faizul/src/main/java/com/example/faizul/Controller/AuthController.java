package com.example.faizul.Controller;

import com.example.faizul.Security.Dto.LoginRequest;
import com.example.faizul.Security.Dto.LoginResponse;
import com.example.faizul.Security.Dto.RegisterRequest;
import com.example.faizul.Security.Dto.RegisterResponse;
import com.example.faizul.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest, request, response);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        RegisterResponse response = authService.register(request);

        return ResponseEntity.ok(response);
    }



}
