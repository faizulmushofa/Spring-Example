package org.example.purejwtexample.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.purejwtexample.Model.User;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId",user.getId())
                .claim("role",user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getName())
                        .toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    private Claims extractAllClaims(String token){

        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }
}
