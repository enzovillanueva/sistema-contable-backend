package com.sistemacontable.usuarios.exception;

import com.sistemacontable.shared.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND,
    USERNAME_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS,
    REGISTRATION_UNAVAILABLE;

    @Override
    public String value() {
        return name();
    }
}
