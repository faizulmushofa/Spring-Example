package org.example.purejwtexample.Dtos;


import java.time.Instant;


public record LoginResponse(
        String message,
        String token,
        Instant expireddAt
) {
}
