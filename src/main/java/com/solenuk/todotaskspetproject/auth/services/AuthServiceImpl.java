package com.solenuk.todotaskspetproject.auth.services;

import com.solenuk.todotaskspetproject.auth.dtos.request.AuthRequestDTO;
import com.solenuk.todotaskspetproject.auth.dtos.response.AuthResponseDTO;
import com.solenuk.todotaskspetproject.auth.services.interfaces.AuthService;
import com.solenuk.todotaskspetproject.auth.services.interfaces.JwtService;
import com.solenuk.todotaskspetproject.user.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.user.dtos.response.ResponseUserDTO;
import com.solenuk.todotaskspetproject.user.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponseDTO register(CreateUserDTO request) {
        ResponseUserDTO created = userService.createUser(request);

        UserDetails principal = userDetailsService.loadUserByUsername(created.email());
        String jwtToken = jwtService.generateToken(principal);
        return new AuthResponseDTO(jwtToken, created);
    }

    @Override
    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        ResponseUserDTO user = userService.getUserByEmail(request.email());
        UserDetails principal = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(principal);
        return new AuthResponseDTO(token, user);
    }
}
