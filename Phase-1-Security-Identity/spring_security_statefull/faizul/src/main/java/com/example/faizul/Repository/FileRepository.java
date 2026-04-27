package com.example.faizul.Repository;

import com.example.faizul.Model.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<StoredFile,Long> {
    List<StoredFile> findByUserUserId(Long userId);
}
