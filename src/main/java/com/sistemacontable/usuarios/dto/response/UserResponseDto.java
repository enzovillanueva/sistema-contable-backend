package com.sistemacontable.usuarios.dto.response;

import java.util.List;

public record UserResponseDto(
        String username,
        String email,
        List<Long> rolesId
) {
}
