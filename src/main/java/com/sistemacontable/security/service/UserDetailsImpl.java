package com.sistemacontable.security.service;

import com.sistemacontable.usuarios.entity.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record UserDetailsImpl(
        User user,
        List<GrantedAuthority> authorities
) implements UserDetails {

    @Override
    public @NullMarked Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @NullMarked String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public @NullMarked String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    public Long getId() {
        return user.getId();
    }
}
