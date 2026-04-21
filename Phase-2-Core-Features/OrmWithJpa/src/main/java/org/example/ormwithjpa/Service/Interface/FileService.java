package org.example.ormwithjpa.Service.Interface;

import org.example.ormwithjpa.Dto.request.DownloadRequest;
import org.example.ormwithjpa.Dto.request.UploadRequest;
import org.example.ormwithjpa.Dto.response.DownloadResponse;
import org.example.ormwithjpa.Dto.response.FileResponse;
import org.example.ormwithjpa.Dto.response.UploadResponse;

import java.util.List;

public interface FileService {

    FileResponse getFile(Long Id);
    FileResponse getFileByUser();
    List<FileResponse> getAllFiles();
    List<FileResponse> getAllFilesByUser(Long id);
    UploadResponse upload(UploadRequest uploadRequest);
    DownloadResponse download(DownloadRequest downloadRequest);
    String softDeleteFile(Long fileId, Long userId);
    String restoreFile(Long fileId, Long userId);

}
