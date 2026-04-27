package org.example.jwtexample.Dto;

import java.util.List;

public record AdminUserDto(
    Long id,
    String email,
    boolean active,
    List<String> roles,
    List<String> permissions,
    int courseCount
) {}
