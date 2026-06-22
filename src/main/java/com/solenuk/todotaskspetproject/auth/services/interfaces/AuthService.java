package com.solenuk.todotaskspetproject.auth.services.interfaces;

import com.solenuk.todotaskspetproject.auth.dtos.request.AuthRequestDTO;
import com.solenuk.todotaskspetproject.auth.dtos.response.AuthResponseDTO;
import com.solenuk.todotaskspetproject.user.dtos.request.CreateUserDTO;

public interface AuthService {
    AuthResponseDTO register(CreateUserDTO request);

    AuthResponseDTO authenticate(AuthRequestDTO request);
}
