package com.solenuk.todotaskspetproject.user.dtos.response;

import com.solenuk.todotaskspetproject.user.enums.UserRole;
import lombok.Builder;

@Builder
public record ResponseUserDTO(
    Integer id,
    String firstName,
    String lastName,
    String email,
    UserRole role) {
}
