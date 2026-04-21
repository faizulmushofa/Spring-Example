package org.example.ormwithjpa.Dto.response;

public record LoginResponse(
        Long userId,
        String username,
        String email,
        String token
    ) {
}
