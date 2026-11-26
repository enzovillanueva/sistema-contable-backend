package com.sistemacontable.security.controller;

import com.sistemacontable.security.auth.JwtUtils;
import com.sistemacontable.security.service.UserDetailsImpl;
import com.sistemacontable.usuarios.dto.request.LoginRequestDto;
import com.sistemacontable.usuarios.dto.response.UserResponseDto;
import com.sistemacontable.usuarios.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(
            @RequestBody LoginRequestDto request,
            HttpServletResponse response
    ) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        if (!(auth.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new IllegalStateException(
                    "El principal autenticado no es del tipo esperado"
            );
        }

        Long userId = userDetails.getId();
        userService.recordLogin(userId);
        UserResponseDto userResponseDto = userService.getUserById(userId);

        String token = JwtUtils.generateToken(userId);
        JwtUtils.addTokenToCookie(response, token);

        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(@AuthenticationPrincipal UserDetailsImpl userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        UserResponseDto userResponseDto = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        JwtUtils.clearToken(response);
        return ResponseEntity.noContent().build();
    }
}
