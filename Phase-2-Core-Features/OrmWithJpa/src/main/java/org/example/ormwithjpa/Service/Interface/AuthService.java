package org.example.ormwithjpa.Service.Interface;

import org.example.ormwithjpa.Dto.request.LoginRequest;
import org.example.ormwithjpa.Dto.request.RegisterRequest;
import org.example.ormwithjpa.Dto.response.LoginResponse;
import org.example.ormwithjpa.Dto.response.RegisterResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);
    RegisterResponse register(RegisterRequest request);
}
