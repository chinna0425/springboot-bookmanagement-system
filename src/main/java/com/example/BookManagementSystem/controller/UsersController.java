package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.UserLoginRequestDto;
import com.example.BookManagementSystem.dto.UserResponseDto;
import com.example.BookManagementSystem.dto.UserSignupRequestDto;
import com.example.BookManagementSystem.service.UsersService;
import com.example.BookManagementSystem.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtService jwtService;

    // signup
    @PostMapping("/user/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequestDto dto) {
        try {
            UserResponseDto resp = usersService.registerUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        }
    }

    // login -> returns token and user
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto dto) {
        try {
            UserResponseDto resp = usersService.loginUser(dto.getUsernameOrEmail(), dto.getPassword());
            String token = jwtService.generateJwtToken(dto.getUsernameOrEmail().trim());
            return ResponseEntity.ok(Map.of("token", token, "user", resp));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        }
    }

    // list all users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<UserResponseDto> list = usersService.getAllUsers();
        return ResponseEntity.ok(list);
    }

    // get by id
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable("id") Long id) {
        UserResponseDto dto = usersService.getUserById(id);
        if (dto == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(dto);
    }

    // update
    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable("id") Long id,
                                                  @RequestBody UserSignupRequestDto dto) {
        UserResponseDto updated = usersService.updateUser(id, dto);
        if (updated == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(updated);
    }

    // delete
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

