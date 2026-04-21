package org.example.ormwithjpa.Dto.response;

import java.time.LocalDateTime;

public record FileResponse(
    Long id,
    String originalName,
    Long size,
    LocalDateTime createdAt,
    boolean isRemoved
) {}
