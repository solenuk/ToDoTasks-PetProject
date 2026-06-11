package com.solenuk.todotaskspetproject.dtos.response;

import com.solenuk.todotaskspetproject.enums.UserRole;
import lombok.Builder;

@Builder
public record ResponseUserDTO(
    Integer id,
    String firstName,
    String lastName,
    String email,
    UserRole role) {
}
