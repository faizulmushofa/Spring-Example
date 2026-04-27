package org.example.jwtexample.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.jwtexample.Model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET ;

    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId",user.getId())
                .claim("roles",user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000 ))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    private Claims extractAllClaims(String token){

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public String extractEmail(String token){
        return  extractAllClaims(token).getSubject();
    }

}
