package com.solenuk.todotaskspetproject.services.impl;

import com.solenuk.todotaskspetproject.dtos.request.AuthRequestDTO;
import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.AuthResponseDTO;
import com.solenuk.todotaskspetproject.entities.User;
import com.solenuk.todotaskspetproject.repositories.UserRepository;
import com.solenuk.todotaskspetproject.services.AuthService;
import com.solenuk.todotaskspetproject.services.JwtService;
import com.solenuk.todotaskspetproject.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public AuthResponseDTO register(CreateUserDTO request) {
        userService.createUser(request);

        User savedUser = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new EntityNotFoundException("User not found after creation."));

        String jwtToken = jwtService.generateToken(savedUser);
        return new AuthResponseDTO(jwtToken);
    }

    @Override
    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + request.email()));

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDTO(jwtToken);
    }
}
