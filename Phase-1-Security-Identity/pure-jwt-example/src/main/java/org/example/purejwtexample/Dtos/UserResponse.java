package org.example.purejwtexample.Dtos;

import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String email,
        List<String> roles
) { }
