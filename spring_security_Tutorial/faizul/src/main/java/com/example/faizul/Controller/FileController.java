package com.example.faizul.Controller;

import com.example.faizul.Security.Dto.FileDto;
import com.example.faizul.Service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import java.util.List;

import com.example.faizul.Model.UserDetailsImp;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;

@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    

    @GetMapping("/all")
    ResponseEntity<List<FileDto>> getAllFileByUserId(@AuthenticationPrincipal UserDetailsImp userDetails){
        List<FileDto> response = fileService.getAllFileByUserId(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileId}")
    ResponseEntity<FileDto> getFileById(@PathVariable Long fileId, @AuthenticationPrincipal UserDetailsImp userDetails){
        FileDto response = fileService.getFileById(fileId,userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetailsImp userDetails) {
        try {
            fileService.saveFile(file, userDetails.getUserId());
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, @AuthenticationPrincipal UserDetailsImp userDetails) {
        Resource resource = fileService.loadFileAsResource(fileId, userDetails.getUserId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
