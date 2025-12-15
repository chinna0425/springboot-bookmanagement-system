package com.example.BookManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherCreateRequestDto {

    @NotBlank(message = "Publisher name is required") // validation
    private String publisherName;
}