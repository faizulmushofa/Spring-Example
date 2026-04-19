package com.example.faizul.Security.Dto;

import java.util.Set;

public record RegisterRequest(
        String userName, String userEmail, String userPassword, Set<String> roles
) {
}
