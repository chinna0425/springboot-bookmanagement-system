package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.dto.UserCreateRequestDto;
import com.example.BookManagementSystem.dto.UserResponseDto;
import com.example.BookManagementSystem.dto.UserUpdateRequestDto;
import com.example.BookManagementSystem.exception.BadRequestException;
import com.example.BookManagementSystem.exception.ConflictException;
import com.example.BookManagementSystem.exception.ResourceNotFoundException;
import com.example.BookManagementSystem.model.Users;
import com.example.BookManagementSystem.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UserJpaRepository userJpaRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    // Register
    public UserResponseDto registerUser(UserCreateRequestDto dto) {

        if (userJpaRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        Users user = new Users(dto.getUserName(), dto.getEmail(), passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        Users saved = userJpaRepository.save(user);
        return mapToResponse(saved);
    }

    // Login
    public Users loginUser(String emailOrUsername, String rawPassword) {

        Users user = userJpaRepository.findByEmail(emailOrUsername)
                .or(() -> userJpaRepository.findByUserName(emailOrUsername))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        return user;
    }

    public List<UserResponseDto> getAllUsers() {
        return userJpaRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponseDto getUserById(Long id) {
        Users user = userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    public UserResponseDto updateUser(Long id, UserUpdateRequestDto dto) {

        if (dto.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        Users user = userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getUserName() != null) {
            user.setUserName(dto.getUserName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        return mapToResponse(userJpaRepository.save(user));
    }


    public void deleteUser(Long id) {
        if (!userJpaRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userJpaRepository.deleteById(id);
    }

    private UserResponseDto mapToResponse(Users u) {
        return new UserResponseDto(u.getId(), u.getUserName(), u.getEmail(), u.getCreatedAt());
    }
}
