package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.dto.UserSignupRequestDto;
import com.example.BookManagementSystem.dto.UserResponseDto;
import com.example.BookManagementSystem.model.Users;
import com.example.BookManagementSystem.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UserJpaRepository userJpaRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    // Register
    public UserResponseDto registerUser(UserSignupRequestDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body required");
        }

        String email = dto.getEmail();
        String name = dto.getUserName();
        String rawPassword = dto.getPassword();

        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (rawPassword == null || rawPassword.length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 4 characters");
        }

        if (userJpaRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        String hashed = passwordEncoder.encode(rawPassword);
        Users toSave = new Users(name, email, hashed);
        Users saved = userJpaRepository.save(toSave);
        return mapToResponse(saved);
    }

    // Login (allows email or username)
    public UserResponseDto loginUser(String emailOrUsername, String rawPassword) {
        if (emailOrUsername == null || rawPassword == null) {
            throw new IllegalArgumentException("Email and password required");
        }

        Optional<Users> foundOpt = userJpaRepository.findByEmail(emailOrUsername);
        if (!foundOpt.isPresent()) foundOpt = userJpaRepository.findByUserName(emailOrUsername);

        if (!foundOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        Users found = foundOpt.get();
        if (!passwordEncoder.matches(rawPassword, found.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return mapToResponse(found);
    }

    public List<UserResponseDto> getAllUsers() {
        List<Users> users = userJpaRepository.findAll();
        List<UserResponseDto> result = new ArrayList<>();
        for (Users u : users) {
            result.add(mapToResponse(u));
        }
        return result;
    }

    public UserResponseDto getUserById(Long id) {
        Optional<Users> userOpt = userJpaRepository.findById(id);
        return userOpt.map(this::mapToResponse).orElse(null);
    }

    public UserResponseDto updateUser(Long id, UserSignupRequestDto dto) {
        Optional<Users> existingOpt = userJpaRepository.findById(id);
        if (existingOpt.isEmpty()) return null;

        Users existing = existingOpt.get();
        existing.setUserName(dto.getUserName() != null ? dto.getUserName() : existing.getUserName());
        existing.setEmail(dto.getEmail() != null ? dto.getEmail() : existing.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        Users updated = userJpaRepository.save(existing);
        return mapToResponse(updated);
    }

    public void deleteUser(Long id){
        userJpaRepository.deleteById(id);
    }

    private UserResponseDto mapToResponse(Users u) {
        if (u == null) return null;
        return new UserResponseDto(u.getId(), u.getUserName(), u.getEmail(), u.getCreatedAt());
    }
}
