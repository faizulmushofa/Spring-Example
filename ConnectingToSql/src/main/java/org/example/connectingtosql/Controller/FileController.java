package org.example.connectingtosql.Controller;

import lombok.RequiredArgsConstructor;
import org.example.connectingtosql.Model.StoredFile;
import org.example.connectingtosql.Service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<?> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFileById(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFileById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFilesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(fileService.getFilesByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestBody StoredFile file) {
        return ResponseEntity.ok(fileService.uploadFile(file));
    }

    @PutMapping
    public ResponseEntity<?> updateFile(@RequestBody StoredFile file) {
        return ResponseEntity.ok(fileService.updateFile(file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.deleteFile(id));
    }
}
