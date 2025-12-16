package com.example.BookManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateRequestDto {

    @NotBlank(message = "Book name must not be empty")
    private String name;

    private String imageUrl;

    @NotNull(message = "Publisher id is required")
    @Positive(message = "Publisher id must be positive")
    private Integer publisherId;
    @NotNull(message = "Author ids is required")
    private List<@Positive(message = "Author id must be positive") Integer> authorIds;
}

