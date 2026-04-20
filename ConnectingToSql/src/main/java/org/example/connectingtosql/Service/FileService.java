package org.example.connectingtosql.Service;

import lombok.RequiredArgsConstructor;
import org.example.connectingtosql.Model.StoredFile;
import org.example.connectingtosql.Repository.FileRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    // Ambil semua file yang belum dihapus
    public List<StoredFile> getAllFiles() {
        return fileRepository.findAll();
    }

    // Ambil file berdasarkan id
    public StoredFile getFileById(Long id) {
        return fileRepository.findById(id);
    }

    // Ambil semua file milik user tertentu
    public List<StoredFile> getFilesByUserId(Long userId) {
        return fileRepository.findByUserId(userId);
    }

    // Simpan file baru — hashName di-generate otomatis
    public Map<String, Object> uploadFile(StoredFile file) {
        Map<String, Object> response = new HashMap<>();

        // Generate hash name unik dari UUID + ekstensi file asli
        String originalName = file.getNameFile() != null ? file.getNameFile() : "file";
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String generatedHash = java.util.UUID.randomUUID().toString().replace("-", "") + extension;
        file.setHashName(generatedHash);

        int result = fileRepository.save(file);
        response.put("success", result > 0);
        response.put("message", result > 0 ? "File berhasil disimpan." : "Gagal menyimpan file.");
        response.put("hashName", generatedHash);
        return response;
    }

    // Update informasi file
    public Map<String, Object> updateFile(StoredFile file) {
        Map<String, Object> response = new HashMap<>();
        int result = fileRepository.update(file);
        response.put("success", result > 0);
        response.put("message", result > 0 ? "File berhasil diupdate." : "Gagal mengupdate file.");
        return response;
    }

    // Soft delete file
    public Map<String, Object> deleteFile(Long id) {
        Map<String, Object> response = new HashMap<>();
        int result = fileRepository.softDelete(id);
        response.put("success", result > 0);
        response.put("message", result > 0 ? "File berhasil dihapus." : "Gagal menghapus file.");
        return response;
    }
}
