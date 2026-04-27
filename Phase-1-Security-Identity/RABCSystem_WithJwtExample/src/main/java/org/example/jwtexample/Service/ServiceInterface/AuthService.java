package org.example.jwtexample.Service.ServiceInterface;

import org.example.jwtexample.Dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    RegisterResponse register(RegisterRequest registerRequest);
}
