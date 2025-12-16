package com.example.BookManagementSystem.dto;

import com.example.BookManagementSystem.model.Publisher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorBooksDto {
    private Integer bookId;
    private String bookName;
    private String imageUrl;
}
