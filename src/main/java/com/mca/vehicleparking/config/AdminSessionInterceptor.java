package com.mca.vehicleparking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mca.vehicleparking.dto.ApiErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Component
public class AdminSessionInterceptor implements HandlerInterceptor {

    public static final String ADMIN_ID = "ADMIN_ID";

    private final ObjectMapper objectMapper;

    public AdminSessionInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(ADMIN_ID) != null) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpServletResponse.SC_UNAUTHORIZED,
                "Please log in to continue.",
                null
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
        return false;
    }
}
