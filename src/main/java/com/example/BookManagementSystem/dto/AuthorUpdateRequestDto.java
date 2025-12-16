package com.example.BookManagementSystem.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class AuthorUpdateRequestDto {

    private String authorName; // optional

    private List<@Positive(message = "Book id must be positive") Integer> bookIds;
    public boolean isEmpty() {
        return authorName == null &&(bookIds ==null || bookIds.isEmpty());
    }
}