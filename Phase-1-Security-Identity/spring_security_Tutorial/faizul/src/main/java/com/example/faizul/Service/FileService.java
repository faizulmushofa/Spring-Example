package com.example.faizul.Service;

import com.example.faizul.Model.StoredFile;
import com.example.faizul.Model.User;
import com.example.faizul.Repository.FileRepository;
import com.example.faizul.Repository.UserRepository;
import com.example.faizul.Security.Dto.*;
import com.example.faizul.Utils.GeneratingName;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final StorageService storageService;
    private final UserRepository userRepository;

    public List<FileDto> getAllFileByUserId(Long userId) {
        return fileRepository.findByUserUserId(userId).stream()
                .map(file -> new FileDto(file.getId(), file.getOriginalName(), file.getFileSize()))
                .collect(Collectors.toList());
    }

    public FileDto getFileById(Long fileId, Long userId) {
        StoredFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
        
        if (!file.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to file");
        }
        
        return new FileDto(file.getId(), file.getOriginalName(), file.getFileSize());
    }

    public void saveFile(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String hashName = GeneratingName.generateUniqueName(file.getOriginalFilename());
        StoredFile storedFile = storageService.storeFile(user, file, hashName);
        fileRepository.save(storedFile);
    }

    public Resource loadFileAsResource(Long fileId, Long userId) {
        StoredFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to file");
        }

        return storageService.loadResource(file);
    }

    public UploadResponse upload(UploadRequest req) {
        if (req == null || req.file() == null) {
            throw new IllegalArgumentException("Request is empty!");
        }

        saveFile(req.file(), req.userId());
        return new UploadResponse(req.userId(), req.file().getOriginalFilename(), req.file().getSize());
    }

    public DeleteResponse delete(DeleteRequest request) {
        StoredFile file = fileRepository.findById(request.fileId())
                .orElseThrow(() -> new RuntimeException("File not found"));

        storageService.moveToTrash(file);
        fileRepository.delete(file); // Pastikan hapus dari database juga

        return new DeleteResponse(file.getId(), "File Deleted");
    }
}