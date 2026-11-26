package com.sistemacontable.security.auth;

import com.sistemacontable.security.service.CustomUserDetailsService;
import com.sistemacontable.security.auth.exception.JwtAuthenticationEntryPoint;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService,
                                   JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "POST".equals(request.getMethod())
                && ("/api/auth/login".equals(request.getServletPath())
                    || "/api/users".equals(request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = JwtUtils.getTokenFromRequest(request);
        if (token != null) {
            try {

                Long userId = JwtUtils.getUserIdFromToken(token);

                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                new AccountStatusUserDetailsChecker().check(userDetails);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | IllegalArgumentException | AuthenticationException e) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(request, response,
                        new BadCredentialsException("Token inválido o usuario no disponible", e));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
