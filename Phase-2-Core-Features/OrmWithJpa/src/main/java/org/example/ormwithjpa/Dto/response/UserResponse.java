package org.example.ormwithjpa.Dto.response;


public record UserResponse(
    Long id,
    String username,
    String firstName,
    String lastName,
    String email
) {}
