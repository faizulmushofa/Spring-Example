package org.example.ormwithjpa.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.ormwithjpa.Dto.request.DownloadRequest;
import org.example.ormwithjpa.Dto.request.UploadRequest;
import org.example.ormwithjpa.Dto.response.DownloadResponse;
import org.example.ormwithjpa.Dto.response.FileResponse;
import org.example.ormwithjpa.Dto.response.UploadResponse;
import org.example.ormwithjpa.Model.StoredFile;
import org.example.ormwithjpa.Model.User;
import org.example.ormwithjpa.Repository.StoredFileRepository;
import org.example.ormwithjpa.Repository.UserRepository;
import org.example.ormwithjpa.Service.Interface.FileService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class FileServiceImp implements FileService {

    private final StoredFileRepository fileRepository;
    private final StorageServicesImp storageServices;
    private final UserRepository userRepository;

    @Override
    public FileResponse getFile(Long Id) {

        StoredFile file = fileRepository.findById(Id).get();

        return new FileResponse(
                file.getId(),
                file.getOriginalName(),
                file.getSize(),
                file.getCreatedAt(),
                file.isRemoved()
        );
    }

    @Override
    public FileResponse getFileByUser() {
        return null;
    }

    @Override
    public List<FileResponse> getAllFiles() {
        return fileRepository.findAll()
                .stream().map(
                        e -> new FileResponse(
                                e.getId(),
                                e.getOriginalName(),
                                e.getSize(),
                                e.getCreatedAt(),
                                e.isRemoved()
                        )
                ).toList();
    }

    @Override
    public List<FileResponse> getAllFilesByUser(Long id) {
        return fileRepository.findAll()
                .stream().filter( file -> file.getUser().getId().equals(id))
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getOriginalName(),
                        file.getSize(),
                        file.getCreatedAt(),
                        file.isRemoved()
                        )).toList();
    }

    @Override
    public UploadResponse upload(UploadRequest uploadRequest) {

        if (userRepository.findById(uploadRequest.userId()).isEmpty()){
            throw new RuntimeException("User Id doesnt Valid!");
        }

        User user = userRepository.findById(uploadRequest.userId()).get();

        String hashName = UUID.randomUUID().toString();

        Path path = storageServices.store(uploadRequest.userId()
        ,uploadRequest.file(),hashName);

        StoredFile newfile = StoredFile.builder()
                .originalName(uploadRequest.file().getOriginalFilename())
                .hashName(hashName)
                .user(user)
                .path(path.toString())
                .size(uploadRequest.file().getSize()).build();
        fileRepository.save(newfile);

        return new UploadResponse("File Berhasil di upload!", newfile.getOriginalName(), newfile.getSize());
    }

    @Override
    public DownloadResponse download(DownloadRequest downloadRequest) {

        StoredFile file = fileRepository.findById(downloadRequest.fileId())
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getUser().getId().equals(downloadRequest.userId())){
            throw new RuntimeException("Unauthorized access to file");
        }

        Resource resource = storageServices.load(file);

        return new DownloadResponse(
                file.getOriginalName(),
                "File Sucessfully download",
                resource
        );


    }

    public String softDeleteFile(Long fileId, Long userId) {
        StoredFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to file");
        }

        file.setRemoved(true);
        fileRepository.save(file);
        return "File berhasil dipindahkan ke tempat sampah.";
    }

    public String restoreFile(Long fileId, Long userId) {
        StoredFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to file");
        }

        file.setRemoved(false);
        fileRepository.save(file);
        return "File berhasil direstore.";
    }
}
