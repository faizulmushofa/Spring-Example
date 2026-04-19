package com.example.faizul.Utils;

import java.util.UUID;

public class GeneratingName {

    public static String generateUniqueName(String originalFileName) {
        String extension = "";
        
        // Mengambil ekstensi file (misal: .jpg, .pdf)
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        // Menggabungkan UUID dengan ekstensi
        return UUID.randomUUID().toString() + extension;
    }
}
