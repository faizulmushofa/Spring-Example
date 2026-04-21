package org.example.ormwithjpa.Dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UploadRequest(
    Long userId,
    String fileName,
    MultipartFile file
) {}
