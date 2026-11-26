package com.sistemacontable.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Un error de aplicación esperado con un código estable y un mensaje público. */
@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorCode code;

    public ApiException(HttpStatus status, ErrorCode code, String detail) {
        super(detail);
        this.status = status;
        this.code = code;
    }

}
