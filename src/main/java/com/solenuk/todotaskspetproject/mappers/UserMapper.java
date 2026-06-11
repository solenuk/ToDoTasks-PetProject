package com.solenuk.todotaskspetproject.mappers;

import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseUserDTO;
import com.solenuk.todotaskspetproject.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(CreateUserDTO request, String encodedPassword) {
        return User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .password(encodedPassword)
            .role(request.role())
            .build();
    }

    public ResponseUserDTO toResponse(User user) {
        return new ResponseUserDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
