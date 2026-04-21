package org.example.ormwithjpa.Service;

import jakarta.transaction.Transactional;
import org.example.ormwithjpa.Model.StoredFile;
import org.example.ormwithjpa.Service.Interface.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Transactional
public class StorageServicesImp implements StorageService {

    private final Path fileStorageLocation = Path.of("./Data/DummyData");
    private final Path TrashLocation = Path.of("./Data/Trash");

    @Override
    public Path store(Long id, MultipartFile file,String hashname) {

        Path userpath = fileStorageLocation.resolve(id.toString());

        try{
            Files.createDirectories(userpath);
            Files.copy(file.getInputStream(),userpath.resolve(hashname));
            return userpath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean delete(StoredFile file) {

        try{
            Path source = Path.of(file.getPath())
                    .resolve(file.getHashName()).normalize();

            Path trashUserDir = TrashLocation.resolve(file.getUser().toString());

            Files.createDirectories(trashUserDir);

            Path target = trashUserDir.resolve(file.getHashName());

            Files.move(source,target);

        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public Resource load(StoredFile file) {
        try{
            Path downloadPath = Path.of(file.getPath()).resolve(file.getHashName());
            Resource resource = new UrlResource(downloadPath.toUri());
            if (!resource.exists() || !resource.isReadable()){
                throw new RuntimeException("File Not Found or Not Readeble");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean restore(StoredFile file) {
        try{
            Path source = Path.of(file.getPath())
                    .resolve(file.getHashName()).normalize();

            Path userDir = fileStorageLocation.resolve(file.getUser().toString());

            Path target = userDir.resolve(file.getHashName());

            Files.move(source,target);
            return true;

        } catch (RuntimeException | IOException e) {
            return false;
        }
    }
}
