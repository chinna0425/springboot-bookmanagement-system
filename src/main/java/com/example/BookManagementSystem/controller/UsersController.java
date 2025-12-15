package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.UserLoginRequestDto;
import com.example.BookManagementSystem.dto.UserResponseDto;
import com.example.BookManagementSystem.dto.UserCreateRequestDto;
import com.example.BookManagementSystem.dto.UserUpdateRequestDto;
import com.example.BookManagementSystem.model.UserDetailsImpl;
import com.example.BookManagementSystem.model.Users;
import com.example.BookManagementSystem.service.UsersService;
import com.example.BookManagementSystem.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtService jwtService;

    // create user
    @PostMapping("/user/signup")
    public ResponseEntity<UserResponseDto> signup(
            @Valid @RequestBody UserCreateRequestDto dto) {

        UserResponseDto resp = usersService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // login
    @PostMapping("/user/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody UserLoginRequestDto dto) {

        Users user = usersService.loginUser(
                dto.getUsernameOrEmail(),
                dto.getPassword()
        );

        String token = jwtService.generateJwtToken(
                new UserDetailsImpl(user)
        );

        return ResponseEntity.ok(Map.of("token", token));
    }

    // get all users
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    // get user by id
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usersService.getUserById(id));
    }

    // update user by id
    @PutMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDto dto) {

        return ResponseEntity.ok(usersService.updateUser(id, dto));
    }

    // delete user by id
    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
