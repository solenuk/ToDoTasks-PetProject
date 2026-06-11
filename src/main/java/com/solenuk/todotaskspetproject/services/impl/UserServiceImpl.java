package com.solenuk.todotaskspetproject.services.impl;

import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseUserDTO;
import com.solenuk.todotaskspetproject.entities.User;
import com.solenuk.todotaskspetproject.mappers.UserMapper;
import com.solenuk.todotaskspetproject.repositories.UserRepository;
import com.solenuk.todotaskspetproject.services.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseUserDTO createUser(CreateUserDTO createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new EntityExistsException("Email already exists!");
        }

        String hashedPassword = passwordEncoder.encode(createUserRequest.password());
        User newUser = userMapper.toEntity(createUserRequest, hashedPassword);
        User savedUser = userRepository.save(newUser);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public ResponseUserDTO updateUser(Integer id, UpdateUserDTO updateUserRequest) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (!existingUser.getEmail().equals(updateUserRequest.email()) && userRepository.existsByEmail(
            updateUserRequest.email())) {
            throw new EntityExistsException("Email already in use!");
        }

        existingUser.setFirstName(updateUserRequest.firstName());
        existingUser.setLastName(updateUserRequest.lastName());
        existingUser.setEmail(updateUserRequest.email());
        existingUser.setRole(updateUserRequest.role());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<ResponseUserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ResponseUserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public ResponseUserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }
}
