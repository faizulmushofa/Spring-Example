package com.example.faizul.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        // Log Request Start
        log.info("--- REQUEST START: [{} {}] ---", request.getMethod(), request.getRequestURI());
        log.info("Host: {} | Connection: {}", request.getHeader("Host"), request.getHeader("Connection"));
        log.info("User-Agent: {}", request.getHeader("User-Agent"));
        log.info("Sec-Fetch: [Site: {}, Mode: {}]", request.getHeader("Sec-Fetch-Site"), request.getHeader("Sec-Fetch-Mode"));
        log.info("Cookie JSESSIONID: {}", getJSessionId(request));

        try {
            filterChain.doFilter(request, response);
        } finally {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = (auth != null) ? auth.getName() : "Anonymous";
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("User: [{}] | Status: [{}] | Duration: [{}ms]", user, response.getStatus(), duration);
            log.info("--- REQUEST END ---");
            System.out.println(); // Beri jarak antar request
        }
    }

    private String getJSessionId(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "None";
    }
}
