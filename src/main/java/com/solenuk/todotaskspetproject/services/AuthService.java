package com.solenuk.todotaskspetproject.services;

import com.solenuk.todotaskspetproject.dtos.request.AuthRequestDTO;
import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(CreateUserDTO request);

    AuthResponseDTO authenticate(AuthRequestDTO request);
}
