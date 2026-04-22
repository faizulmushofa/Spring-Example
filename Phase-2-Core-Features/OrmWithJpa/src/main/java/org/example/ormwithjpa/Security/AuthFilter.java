package org.example.ormwithjpa.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // public endpoint bypass
        if (path.startsWith("/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionId = extractSessionId(request);

        if (sessionId == null) {
            log.warn("Missing SESSION_ID cookie for path={}", path);
            unauthorized(response, "Missing session");
            return;
        }

        if (!SessionStore.isValid(sessionId)) {
            log.warn("Invalid sessionId={}", sessionId);
            unauthorized(response, "Invalid session");
            return;
        }

        Long userId = SessionStore.getUserId(sessionId);
        request.setAttribute("userId", userId);

        log.info("Authenticated request userId={} path={}", userId, path);

        filterChain.doFilter(request, response);
    }

    private String extractSessionId(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie c : request.getCookies()) {
            if ("SESSION_ID".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}