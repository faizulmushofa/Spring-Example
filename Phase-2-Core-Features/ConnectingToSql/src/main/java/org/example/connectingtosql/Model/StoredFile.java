package org.example.connectingtosql.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredFile {
    private Long id;
    private Long userId;      // FK ke tabel user
    private String nameFile;
    private String hashName;
    private Long size;
    private Boolean isRemoved;

}
