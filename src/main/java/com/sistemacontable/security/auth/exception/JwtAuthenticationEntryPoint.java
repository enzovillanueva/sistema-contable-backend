package com.sistemacontable.security.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import com.sistemacontable.shared.exception.ApiErrors;
import org.springframework.http.HttpStatus;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException exception) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiErrors.write(response, objectMapper, ApiErrors.create(status, ApiErrors.code(status),
                ApiErrors.detail(status), request.getRequestURI()));
    }
}
