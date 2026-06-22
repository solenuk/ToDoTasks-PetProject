package com.solenuk.todotaskspetproject.user.services.interfaces;

import com.solenuk.todotaskspetproject.common.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.common.enteties.SecurityUser;
import com.solenuk.todotaskspetproject.user.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.user.dtos.request.UpdateUserDTO;
import com.solenuk.todotaskspetproject.user.dtos.response.ResponseUserDTO;
import java.nio.file.AccessDeniedException;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ResponseUserDTO createUser(CreateUserDTO createUserRequest);

    ResponseUserDTO updateUser(Integer id, UpdateUserDTO updateUserRequest, SecurityUser currentUser)
        throws AccessDeniedException;

    void deleteUser(Integer id, SecurityUser currentUser) throws AccessDeniedException;

    public PaginatedResponseDTO<ResponseUserDTO> getAllUsers(Pageable pageable);

    ResponseUserDTO getUserById(Integer id);

    ResponseUserDTO getUserByEmail(String email);
}
