package com.example.BookManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDto {
    private int id;
    private String name;
    private String imageUrl;
    private PublisherResponseDto publisher;
    private List<AuthorResponseDto> authors;
}

