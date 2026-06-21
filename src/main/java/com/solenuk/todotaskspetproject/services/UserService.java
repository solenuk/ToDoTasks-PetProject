package com.solenuk.todotaskspetproject.services;

import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseUserDTO;
import com.solenuk.todotaskspetproject.entities.User;
import java.nio.file.AccessDeniedException;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ResponseUserDTO createUser(CreateUserDTO createUserRequest);

    ResponseUserDTO updateUser(Integer id, UpdateUserDTO updateUserRequest, User currentUser)
        throws AccessDeniedException;

    void deleteUser(Integer id, User currentUser) throws AccessDeniedException;

    public PaginatedResponseDTO<ResponseUserDTO> getAllUsers(Pageable pageable);

    ResponseUserDTO getUserById(Integer id);

    ResponseUserDTO getUserByEmail(String email);
}
