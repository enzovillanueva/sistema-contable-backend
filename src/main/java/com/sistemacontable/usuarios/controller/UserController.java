package com.sistemacontable.usuarios.controller;

import com.sistemacontable.usuarios.dto.request.UserRegisterRequestDto;
import com.sistemacontable.usuarios.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserRegisterRequestDto userRegister){
        this.userService.registerUser(userRegister);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable("id") Long userId){
        this.userService.activateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable("id") Long userId){
        this.userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}
