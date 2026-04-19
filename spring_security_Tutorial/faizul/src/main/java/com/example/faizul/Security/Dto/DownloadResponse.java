package com.example.faizul.Security.Dto;


import org.springframework.core.io.Resource;

public record DownloadResponse(
    String originalName,
    String path,
    Long fileSize,
    Resource resource
) {}
