package com.solenuk.todotaskspetproject.services;

import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseUserDTO;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ResponseUserDTO createUser(CreateUserDTO createUserRequest);

    ResponseUserDTO updateUser(Integer id, UpdateUserDTO updateUserRequest);

    void deleteUser(Integer id);

    public PaginatedResponseDTO<ResponseUserDTO> getAllUsers(Pageable pageable);

    ResponseUserDTO getUserById(Integer id);

    ResponseUserDTO getUserByEmail(String email);
}
