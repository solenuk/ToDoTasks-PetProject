package com.solenuk.todotaskspetproject.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.*;
import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseUserDTO;
import com.solenuk.todotaskspetproject.entities.User;
import com.solenuk.todotaskspetproject.enums.UserRole;
import com.solenuk.todotaskspetproject.mappers.UserMapper;
import com.solenuk.todotaskspetproject.repositories.UserRepository;
import com.solenuk.todotaskspetproject.services.impl.UserServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final Integer defaultId = 1;
    private final String defaultFirstName = "TestFirstName";
    private final String defaultLastName = "TestLastName";
    private final String defaultEmail = "test@gmail.com";
    private final String defaultPassword = "TestPassword";
    private final UserRole defaultRole = UserRole.USER_ROLE;

    @Test
    @DisplayName("createUser should return DTO when user is created")
    void createUser_ShouldReturnDTO_WhenUserIsCreated() {
        CreateUserDTO createUserRequest = new CreateUserDTO(
            defaultFirstName, defaultLastName, defaultEmail, defaultPassword, defaultRole
        );
        ResponseUserDTO expectedResponse = new ResponseUserDTO(
            defaultId, defaultFirstName, defaultLastName, defaultEmail, defaultRole
        );

        User mockedUser = new User();

        when(repository.existsByEmail(createUserRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(createUserRequest.password())).thenReturn("hashedPassword");
        when(mapper.toEntity(createUserRequest, "hashedPassword")).thenReturn(mockedUser);
        when(repository.save(mockedUser)).thenReturn(mockedUser);
        when(mapper.toResponse(mockedUser)).thenReturn(expectedResponse);

        ResponseUserDTO actualResponse = userService.createUser(createUserRequest);

        assertNotNull(actualResponse);
        assertEquals(defaultEmail, actualResponse.email());

        verify(repository, times(1)).save(mockedUser);
        verify(mapper, times(1)).toResponse(mockedUser);
    }

    @Test
    @DisplayName("createUser should throw EntityExistsException when user exists")
    void createUser_ShouldThrowEntityExistsException_IfUserExists() {
        CreateUserDTO createUserRequest = new CreateUserDTO(
            defaultFirstName, defaultLastName, defaultEmail, defaultPassword, defaultRole
        );

        when(repository.existsByEmail(createUserRequest.email())).thenReturn(true);

        EntityExistsException exception = assertThrows(
            EntityExistsException.class,
            () -> userService.createUser(createUserRequest)
        );

        assertEquals("Email already exists!", exception.getMessage());

        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("getUserById should return DTO when user exists")
    void getUserById_ShouldReturnDTO_IfUserExists() {
        ResponseUserDTO expectedResponse = new ResponseUserDTO(
            defaultId, defaultFirstName, defaultLastName, defaultEmail, defaultRole
        );

        User mockedEntity = new User();

        when(repository.findById(defaultId)).thenReturn(Optional.of(mockedEntity));
        when(mapper.toResponse(mockedEntity)).thenReturn(expectedResponse);

        ResponseUserDTO actualResponse = userService.getUserById(defaultId);

        assertNotNull(actualResponse);
        assertEquals(defaultId, actualResponse.id());
    }

    @Test
    @DisplayName("getUserById should throw EntityNotFoundException when user does not exist")
    void getUserById_ShouldThrowEntityNotFoundException_IfUserDoesNotExist() {
        Integer userId = 99;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> userService.getUserById(userId)
        );

        assertEquals("User not found with id: " + userId, exception.getMessage());
    }

    @Test
    @DisplayName("getAllUsers should return a list of DTOs when users exist")
    void getAllUsers_ShouldReturnListOfDTOs_WhenUsersExist() {
        User mockedUser1 = new User();
        User mockedUser2 = new User();
        List<User> usersList = List.of(mockedUser1, mockedUser2);

        ResponseUserDTO response1 = new ResponseUserDTO(
            1, "First1", "Last1", "test1@gmail.com", UserRole.USER_ROLE
        );
        ResponseUserDTO response2 = new ResponseUserDTO(
            2, "First2", "Last2", "test2@gmail.com", UserRole.USER_ROLE
        );

        when(repository.findAll()).thenReturn(usersList);
        when(mapper.toResponse(mockedUser1)).thenReturn(response1);
        when(mapper.toResponse(mockedUser2)).thenReturn(response2);

        List<ResponseUserDTO> actualResponse = userService.getAllUsers();

        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("updateUser should return updated DTO when successful")
    void updateUser_ShouldReturnUpdatedDTO_WhenSuccessful() {
        String newFirstName = "UpdatedFirstName";

        UpdateUserDTO updateRequest = new UpdateUserDTO(
            newFirstName, defaultLastName, defaultEmail, UserRole.USER_ROLE
        );

        User existingUser = new User();
        existingUser.setEmail(defaultEmail);

        ResponseUserDTO expectedResponse = new ResponseUserDTO(
            defaultId, newFirstName, defaultLastName, defaultEmail, UserRole.USER_ROLE
        );

        when(repository.findById(defaultId)).thenReturn(Optional.of(existingUser));
        when(repository.save(existingUser)).thenReturn(existingUser);
        when(mapper.toResponse(existingUser)).thenReturn(expectedResponse);

        ResponseUserDTO actualResponse = userService.updateUser(defaultId, updateRequest);

        assertNotNull(actualResponse);
        assertEquals(newFirstName, actualResponse.firstName());

        verify(repository, never()).existsByEmail(anyString());
        verify(repository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("updateUser should throw EntityExistsException when new email is already taken")
    void updateUser_ShouldThrowEntityExistsException_WhenNewEmailIsTaken() {
        String oldEmail = "old@gmail.com";
        String takenEmail = "taken@gmail.com";

        UpdateUserDTO updateRequest = new UpdateUserDTO(
            defaultFirstName, defaultLastName, takenEmail, defaultRole
        );

        User existingUser = new User();
        existingUser.setEmail(oldEmail);

        when(repository.findById(defaultId)).thenReturn(Optional.of(existingUser));
        when(repository.existsByEmail(takenEmail)).thenReturn(true);

        EntityExistsException exception = assertThrows(
            EntityExistsException.class,
            () -> userService.updateUser(defaultId, updateRequest)
        );

        assertEquals("Email already in use!", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("deleteUser should call deleteById when user exists")
    void deleteUser_ShouldCallDeleteById_WhenUserExists() {
        when(repository.existsById(defaultId)).thenReturn(true);

        userService.deleteUser(defaultId);

        verify(repository, times(1)).deleteById(defaultId);
    }

    @Test
    @DisplayName("deleteUser should throw EntityNotFoundException when user does not exist")
    void deleteUser_ShouldThrowEntityNotFoundException_IfUserDoesNotExist() {
        Integer userId = 99;

        when(repository.existsById(userId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> userService.deleteUser(userId)
        );

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getUserByEmail should return DTO when user exists")
    void getUserByEmail_ShouldReturnDTO_IfUserExists() {
        ResponseUserDTO expectedResponse = new ResponseUserDTO(
            defaultId, defaultFirstName, defaultLastName, defaultEmail, defaultRole
        );
        User mockedEntity = new User();

        when(repository.findByEmail(defaultEmail)).thenReturn(Optional.of(mockedEntity));
        when(mapper.toResponse(mockedEntity)).thenReturn(expectedResponse);

        ResponseUserDTO actualResponse = userService.getUserByEmail(defaultEmail);

        assertNotNull(actualResponse);
        assertEquals(defaultEmail, actualResponse.email());
    }

    @Test
    @DisplayName("getUserByEmail should throw EntityNotFoundException when user does not exist")
    void getUserByEmail_ShouldThrowEntityNotFoundException_IfUserDoesNotExist() {
        String missingEmail = "nobody@gmail.com";

        when(repository.findByEmail(missingEmail)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> userService.getUserByEmail(missingEmail)
        );

        assertEquals("User not found with email: " + missingEmail, exception.getMessage());

        verify(mapper, never()).toResponse(any());
    }
}
