package org.example.jwtexample.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       // 1. dapatkan headeer
        String authHeader = request.getHeader("Authorization");

        // 2. cek keberadaan header
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        // 3. dapatkan token dan kenali email dari token
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        // 4. dapatkan user dari userdetails Service
        UserDetails user = userDetailsService.loadUserByUsername(email);

        //5. buat auth token
        UsernamePasswordAuthenticationToken auth =new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        // 6. auth token di simpan di context
        SecurityContextHolder.getContext().setAuthentication(auth);

        //7. lakukan filterchain
        filterChain.doFilter(request,response);
    }
}
