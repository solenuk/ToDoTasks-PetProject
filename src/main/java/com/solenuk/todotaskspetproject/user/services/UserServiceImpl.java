package com.solenuk.todotaskspetproject.user.services;

import com.solenuk.todotaskspetproject.common.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.common.mappers.PaginationMapper;
import com.solenuk.todotaskspetproject.user.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.user.dtos.request.UpdateUserDTO;
import com.solenuk.todotaskspetproject.user.dtos.response.ResponseUserDTO;
import com.solenuk.todotaskspetproject.user.entities.User;
import com.solenuk.todotaskspetproject.user.mappers.UserMapper;
import com.solenuk.todotaskspetproject.user.repositories.UserRepository;
import com.solenuk.todotaskspetproject.user.services.interfaces.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PaginationMapper paginationMapper;

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
    public ResponseUserDTO updateUser(Integer id, UpdateUserDTO updateUserRequest, User currentUser)
        throws AccessDeniedException {
        verifyUserPermissions(id, currentUser);
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
    public void deleteUser(Integer id, User currentUser) throws AccessDeniedException {
        verifyUserPermissions(id, currentUser);
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ResponseUserDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return paginationMapper.mapToPaginatedResponse(userPage, userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseUserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseUserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    private void verifyUserPermissions(Integer targetUserId, User currentUser) throws AccessDeniedException {
        boolean isSelf = currentUser.getId().equals(targetUserId);
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN_ROLE");

        if (!isSelf && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to modify this user account.");
        }
    }
}
