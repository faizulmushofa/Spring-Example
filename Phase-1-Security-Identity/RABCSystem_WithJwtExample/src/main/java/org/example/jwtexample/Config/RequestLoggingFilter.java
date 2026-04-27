package org.example.jwtexample.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Ambil info JWT sebelum filter chain
        String authHeader = request.getHeader("Authorization");
        String jwtInfo = "No Token";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Tampilkan 20 karakter pertama
            jwtInfo = "Bearer " + token.substring(0, Math.min(20, token.length())) + "...";
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Ambil user yang terautentikasi setelah filter chain
            String principal = "anonymous";
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                principal = auth.getName();
            }

            logger.info("[{}] {} {} → {} | User: {} | JWT: {} | {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString() != null ? "?" + request.getQueryString() : "",
                    response.getStatus(),
                    principal,
                    jwtInfo,
                    duration);
        }
    }
}
