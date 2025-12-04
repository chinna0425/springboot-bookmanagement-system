package com.example.BookManagementSystem.repository;

import com.example.BookManagementSystem.dto.UserResponseDto;
import com.example.BookManagementSystem.dto.UserSignupRequestDto;

import java.util.List;

public interface UserRepository {
    UserResponseDto registerUser(UserSignupRequestDto dto);

    UserResponseDto loginUser(String emailOrUsername, String rawPassword);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id,UserSignupRequestDto dto);

    void deleteUser(Long id);
}
