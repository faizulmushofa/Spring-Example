package org.example.ormwithjpa.Model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratedPassword {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // encode password (hash)
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // cek password
    public static boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}