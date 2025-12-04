package com.example.BookManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDto {
    private String name;
    private String imageUrl;
    private Integer publisherId;
    private List<Integer> authorIds;
}
