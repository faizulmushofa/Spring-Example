package org.example.ormwithjpa.Repository;

import org.example.ormwithjpa.Model.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredFileRepository extends JpaRepository<StoredFile,Long> {
}
