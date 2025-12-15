package com.example.BookManagementSystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {

    @Size(min = 2, message = "Username must be at least 2 characters")
    private String userName;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    public boolean isEmpty() {
        return userName == null && email == null && password == null;
    }

}

