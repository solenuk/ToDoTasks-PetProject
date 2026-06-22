package com.solenuk.todotaskspetproject.auth.dtos.response;

import com.solenuk.todotaskspetproject.user.dtos.response.ResponseUserDTO;

public record AuthResponseDTO(
    String token,
    ResponseUserDTO user
) {
}
