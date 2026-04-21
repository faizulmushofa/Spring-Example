package org.example.ormwithjpa.Service.Interface;

import org.example.ormwithjpa.Dto.request.LoginRequest;
import org.example.ormwithjpa.Dto.request.RegisterRequest;
import org.example.ormwithjpa.Dto.response.LoginResponse;
import org.example.ormwithjpa.Dto.response.UserResponse;
import org.example.ormwithjpa.Model.User;

import java.util.List;

public interface UserService {

    UserResponse save(User user);
    List<UserResponse> getAllUser();
    UserResponse getUser(Long id);


}
