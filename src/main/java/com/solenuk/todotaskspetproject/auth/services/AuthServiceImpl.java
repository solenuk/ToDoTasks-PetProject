package com.solenuk.todotaskspetproject.auth.services;

import com.solenuk.todotaskspetproject.auth.dtos.request.AuthRequestDTO;
import com.solenuk.todotaskspetproject.auth.dtos.response.AuthResponseDTO;
import com.solenuk.todotaskspetproject.auth.services.interfaces.AuthService;
import com.solenuk.todotaskspetproject.auth.services.interfaces.JwtService;
import com.solenuk.todotaskspetproject.user.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.user.entities.User;
import com.solenuk.todotaskspetproject.user.mappers.UserMapper;
import com.solenuk.todotaskspetproject.user.repositories.UserRepository;
import com.solenuk.todotaskspetproject.user.services.interfaces.UserService;
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
    private final UserMapper userMapper;

    @Override
    public AuthResponseDTO register(CreateUserDTO request) {
        userService.createUser(request);

        User savedUser = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new EntityNotFoundException("User not found after creation."));

        String jwtToken = jwtService.generateToken(savedUser);
        return new AuthResponseDTO(jwtToken, userMapper.toResponse(savedUser));
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
        return new AuthResponseDTO(jwtToken, userMapper.toResponse(user));
    }
}
