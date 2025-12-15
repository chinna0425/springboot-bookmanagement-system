package com.example.BookManagementSystem.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequestDto {

    private String name;

    private String imageUrl;

    @Positive(message = "Publisher id must be positive")
    private Integer publisherId;

    private List<@Positive(message = "Author id must be positive") Integer> authorIds;
    public boolean isEmpty() {
        return name == null && imageUrl == null && publisherId == null && authorIds.isEmpty();
    }
}
