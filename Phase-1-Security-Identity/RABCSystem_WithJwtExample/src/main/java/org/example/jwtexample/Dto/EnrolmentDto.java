package org.example.jwtexample.Dto;

import org.example.jwtexample.Model.Course;
import org.example.jwtexample.Model.Enum.Status;
import java.time.LocalDateTime;

public record EnrolmentDto(
        Long enrolmentId,
        UserDto user,
        Course course,
        Status status,
        LocalDateTime enrollAt
) {}
