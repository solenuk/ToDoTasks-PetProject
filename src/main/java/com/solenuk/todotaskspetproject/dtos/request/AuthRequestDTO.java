package com.solenuk.todotaskspetproject.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
    @NotBlank(message = "Email is required!")
    @Email(message = "Must be a valid email format")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {
}
