package com.sistemacontable.usuarios.service;

import com.sistemacontable.usuarios.exception.UserErrorCode;

import com.sistemacontable.usuarios.dto.request.UserRegisterRequestDto;
import com.sistemacontable.usuarios.dto.response.UserResponseDto;
import com.sistemacontable.usuarios.entity.Rol;
import com.sistemacontable.usuarios.entity.User;
import com.sistemacontable.usuarios.entity.UserRol;
import com.sistemacontable.usuarios.repository.RolRepository;
import com.sistemacontable.usuarios.repository.UserRepository;
import com.sistemacontable.usuarios.repository.UserRolRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.sistemacontable.shared.exception.ApiException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserRolRepository userRolRepository;
    private final RolRepository rolRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserRolRepository userRolRepository,
            PasswordEncoder passwordEncoder,
            RolRepository rolRepository
    ) {
        this.userRepository = userRepository;
        this.userRolRepository = userRolRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
    }

    @Transactional
    public void registerUser(UserRegisterRequestDto userRegister){
        if (userRepository.existsByUsername(userRegister.username())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    UserErrorCode.USERNAME_ALREADY_EXISTS,
                    "El nombre de usuario ya existe."
            );
        }

        if (userRepository.existsByEmail(userRegister.email())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    UserErrorCode.EMAIL_ALREADY_EXISTS,
                    "El email ya existe."
            );
        }

        User user = this.mapToUser(userRegister);

        Rol rol = rolRepository.findByName("CONTADOR")
                .orElseThrow(() -> new ApiException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        UserErrorCode.REGISTRATION_UNAVAILABLE,
                        "El registro no está disponible en este momento."
                ));

        user.activate();
        userRepository.save(user);
        userRolRepository.save(new UserRol(user, rol));
    }

    @Transactional
    public void activateUser(Long userId) {
        findUser(userId).activate();
    }

    @Transactional
    public void deactivateUser(Long userId) {
        findUser(userId).deactivate();
    }

    @Transactional
    public void recordLogin(Long userId) {
        findUser(userId).recordLogin();
    }

    public UserResponseDto getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        UserErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado"
                ));
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

    private User mapToUser(UserRegisterRequestDto user){
        return new User(
                user.username(),
                passwordEncoder.encode(user.password()),
                user.email()
        );
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        UserErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }
}
