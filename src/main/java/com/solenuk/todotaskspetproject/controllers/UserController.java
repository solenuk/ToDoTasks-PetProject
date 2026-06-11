package com.solenuk.todotaskspetproject.controllers;

import com.solenuk.todotaskspetproject.dtos.request.CreateUserDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateUserDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseUserDTO;
import com.solenuk.todotaskspetproject.services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ResponseUserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseUserDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping
    public ResponseEntity<ResponseUserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserRequest) {
        ResponseUserDTO createdUser = userService.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody
        UpdateUserDTO updateUserRequest) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
