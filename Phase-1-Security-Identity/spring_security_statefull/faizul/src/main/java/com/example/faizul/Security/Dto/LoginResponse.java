package com.example.faizul.Security.Dto;

import java.util.Set;

public record LoginResponse(Long userId, String email, Set<String> roles) {
}
