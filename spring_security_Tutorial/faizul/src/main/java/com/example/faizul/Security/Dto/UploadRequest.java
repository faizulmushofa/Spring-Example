package com.example.faizul.Security.Dto;
import org.springframework.web.multipart.MultipartFile;

public record UploadRequest(
    Long userId,
    MultipartFile file
) {}
