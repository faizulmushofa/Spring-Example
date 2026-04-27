package org.example.jwtexample.Dto;

public record LoginResponse(String token, UserDto user) {
}
