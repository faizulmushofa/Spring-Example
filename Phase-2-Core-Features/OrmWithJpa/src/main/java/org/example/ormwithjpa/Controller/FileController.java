package org.example.ormwithjpa.Controller;

import lombok.AllArgsConstructor;
import org.example.ormwithjpa.Dto.request.DownloadRequest;
import org.example.ormwithjpa.Dto.request.UploadRequest;
import org.example.ormwithjpa.Dto.response.DownloadResponse;
import org.example.ormwithjpa.Dto.response.FileResponse;
import org.example.ormwithjpa.Dto.response.UploadResponse;
import org.example.ormwithjpa.Service.Interface.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/{id}")
    public ResponseEntity<FileResponse> getFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFile(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FileResponse>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FileResponse>> getAllFilesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(fileService.getAllFilesByUser(userId));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(@ModelAttribute UploadRequest request) {
        return ResponseEntity.ok(fileService.upload(request));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(
            @PathVariable Long fileId,
            @RequestParam Long userId
    ) {
        DownloadRequest request = new DownloadRequest(fileId, userId);
        DownloadResponse response = fileService.download(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + response.fileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.resource());
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<String> softDeleteFile(
            @PathVariable Long fileId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(fileService.softDeleteFile(fileId, userId));
    }

    @PutMapping("/restore/{fileId}")
    public ResponseEntity<String> restoreFile(
            @PathVariable Long fileId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(fileService.restoreFile(fileId, userId));
    }
}
