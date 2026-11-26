package com.sistemacontable.security.service;

import com.sistemacontable.usuarios.entity.User;
import com.sistemacontable.usuarios.repository.UserRepository;
import com.sistemacontable.usuarios.repository.UserRolRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRolRepository userRolRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            UserRolRepository userRolRepository
    ){
        this.userRepository = userRepository;
        this.userRolRepository = userRolRepository;
    }

    @Override
    public @NullMarked UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado"));

        return buildUserDetails(user);
    }

    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado"));

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorities =
                userRolRepository.findByUser_Id(user.getId())
                        .stream()
                        .map(userRol -> userRol.getRol().getName())
                        .distinct()
                        .<GrantedAuthority>map(name ->
                                new SimpleGrantedAuthority("ROLE_" + name))
                        .toList();

        return new UserDetailsImpl(user, authorities);
    }
}
