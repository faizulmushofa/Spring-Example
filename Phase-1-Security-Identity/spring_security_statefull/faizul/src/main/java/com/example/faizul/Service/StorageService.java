package com.example.faizul.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.example.faizul.Model.StoredFile;
import com.example.faizul.Model.User;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    private final Path fileStorageLocation = Path.of("./Data/DummyData");
    private final Path fileTrashLocation = Path.of("./Data/Trash");

    public StoredFile storeFile(User user, MultipartFile file, String hashName) {

        Path userpath = fileStorageLocation.resolve(user.getUserId().toString());
        
        try {
            Files.createDirectories(userpath);
            Files.copy(file.getInputStream(), userpath.resolve(hashName));
            return new StoredFile(null, hashName, file.getOriginalFilename(), userpath.toString(), false,
                    file.getSize(), user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file : ", e);
        }
    }

    public Resource loadResource(StoredFile file) {
        try {
            Path resolved = Path.of(file.getPath()).resolve(file.getHashName());
            Resource resource = new UrlResource(resolved.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable: " + resolved);
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed load file", e);
        }
    }
    

    public void moveToTrash(StoredFile file) {

        try {
            Path source = Path.of(file.getPath())
                    .resolve(file.getHashName())
                    .normalize();

            Path trashUserDir = fileTrashLocation.resolve(
                    file.getUser().toString());

            Files.createDirectories(trashUserDir);

            Path target = trashUserDir.resolve(file.getHashName());

            Files.move(source, target);

        } catch (IOException e) {
            throw new RuntimeException("Failed move file to trash", e);
        }
    }


}
