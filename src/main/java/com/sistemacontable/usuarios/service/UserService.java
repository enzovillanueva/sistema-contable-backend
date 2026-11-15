package com.sistemacontable.usuarios.service;

import com.sistemacontable.usuarios.dto.response.UserResponseDto;
import com.sistemacontable.usuarios.entity.User;
import com.sistemacontable.usuarios.repository.UserRepository;
import com.sistemacontable.usuarios.repository.UserRolRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserRolRepository userRolRepository;

    public UserService(UserRepository userRepository, UserRolRepository userRolRepository) {
        this.userRepository = userRepository;
        this.userRolRepository = userRolRepository;
    }

    public UserResponseDto getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return mapToUserResponse(user);
    }

    private UserResponseDto mapToUserResponse(User user){
        List<Long> rolesId = userRolRepository.findByUser_Id(user.getId())
                .stream()
                .map(userRol -> userRol.getRol().getId())
                .toList();

        return new UserResponseDto(
                user.getUsername(),
                user.getEmail(),
                rolesId
        );
    }
}
