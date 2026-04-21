package org.example.ormwithjpa.Dto.request;

public record LoginRequest(
    String email,
    String password
) {}
