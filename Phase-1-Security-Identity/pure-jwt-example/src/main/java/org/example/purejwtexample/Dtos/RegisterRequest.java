package org.example.purejwtexample.Dtos;

public record RegisterRequest(
        String username,
        String password,
        String email
) { }
