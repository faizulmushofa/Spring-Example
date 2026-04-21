package org.example.ormwithjpa.Dto.response;

public record UploadResponse(
    String message,
    String originalName,
    Long size
) {}
