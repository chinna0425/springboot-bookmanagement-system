package com.example.BookManagementSystem.repository;

import com.example.BookManagementSystem.dto.UserResponseDto;
import com.example.BookManagementSystem.dto.UserCreateRequestDto;

import java.util.List;

public interface UserRepository {
    UserResponseDto registerUser(UserCreateRequestDto dto);

    UserResponseDto loginUser(String emailOrUsername, String rawPassword);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserCreateRequestDto dto);

    void deleteUser(Long id);
}
