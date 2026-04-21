package org.example.ormwithjpa.Dto.request;



public record RegisterRequest(
    String username,
    String firstName,
    String lastName,
    String email,
    String password
) {}
