package org.example.purejwtexample.Dtos;

import org.springframework.boot.web.server.Cookie;

public record LoginRequest (
        String username,
        String password
){ }
