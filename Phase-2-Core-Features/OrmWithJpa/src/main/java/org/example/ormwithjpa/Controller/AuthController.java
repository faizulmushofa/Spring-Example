package org.example.ormwithjpa.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.ormwithjpa.Dto.request.LoginRequest;
import org.example.ormwithjpa.Dto.request.RegisterRequest;
import org.example.ormwithjpa.Dto.response.LoginResponse;
import org.example.ormwithjpa.Dto.response.RegisterResponse;
import org.example.ormwithjpa.Service.AuthServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/Auth")
public class AuthController {

    private AuthServiceImp authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req,
                                               HttpServletResponse response) {
        LoginResponse result = authService.login(req);

        Cookie cookie = new Cookie("SESSION_ID", result.token());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
