package org.example.ormwithjpa.Service.Interface;

import org.example.ormwithjpa.Model.StoredFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {

    Path store(Long id, MultipartFile file, String hashName);
    boolean delete(StoredFile file);
    Resource load(StoredFile file);
    boolean restore(StoredFile file);


}
