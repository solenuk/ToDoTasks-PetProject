package com.solenuk.todotaskspetproject.services;

import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseUserDTO;
import java.util.List;

public interface UserService {
    ResponseUserDTO createUser(CreateUserDTO createUserRequest);

    ResponseUserDTO updateUser(Integer id, UpdateUserDTO updateUserRequest);

    void deleteUser(Integer id);

    List<ResponseUserDTO> getAllUsers();

    ResponseUserDTO getUserById(Integer id);

    ResponseUserDTO getUserByEmail(String email);
}
