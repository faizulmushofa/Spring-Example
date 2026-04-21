package org.example.ormwithjpa.Dto.request;

public record DownloadRequest(
    Long fileId,
    Long userId
) {}
