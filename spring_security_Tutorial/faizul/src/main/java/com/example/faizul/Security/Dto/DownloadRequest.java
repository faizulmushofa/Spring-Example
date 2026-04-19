package com.example.faizul.Security.Dto;

public record DownloadRequest(
    Long userId,
    Long fileId,
    String originalName
) {}
