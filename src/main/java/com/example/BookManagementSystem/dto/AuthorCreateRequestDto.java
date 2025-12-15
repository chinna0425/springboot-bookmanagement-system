package com.example.BookManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCreateRequestDto {

    @NotBlank(message = "Author name is required") // ADDED
    private String authorName;


    private List<@Positive(message = "Book id must be positive") Integer> bookIds;
}

