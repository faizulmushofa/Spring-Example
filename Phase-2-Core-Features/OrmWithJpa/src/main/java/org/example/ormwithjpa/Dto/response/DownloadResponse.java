package org.example.ormwithjpa.Dto.response;


import org.springframework.core.io.Resource;

public record DownloadResponse(
        String fileName,
        String message,
        Resource resource

) {}
