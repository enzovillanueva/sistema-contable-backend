package com.sistemacontable.security.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import com.sistemacontable.shared.exception.ApiErrors;
import org.springframework.http.HttpStatus;

@Component
public class JwtAccessDenierHandle implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public JwtAccessDenierHandle(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AccessDeniedException exception) throws IOException {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrors.write(response, objectMapper, ApiErrors.create(status, ApiErrors.code(status),
                ApiErrors.detail(status), request.getRequestURI()));
    }
}
