package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.AuthorCreateRequestDto;
import com.example.BookManagementSystem.dto.AuthorResponseDto;
import com.example.BookManagementSystem.dto.AuthorUpdateRequestDto;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    // Get all authors
    @GetMapping("/authors")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<AuthorResponseDto>> getAuthors() {
        return ResponseEntity.ok(authorService.getAuthors());
    }

    // Get By Id
    @GetMapping("/authors/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable int id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    // add
    @PostMapping("/authors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> addAuthor(
            @Valid @RequestBody AuthorCreateRequestDto req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authorService.addAuthor(req));
    }

    // update
    @PutMapping("/authors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> updateAuthor(
            @PathVariable int id,
            @Valid @RequestBody AuthorUpdateRequestDto req) {

        return ResponseEntity.ok(authorService.updateAuthor(id, req));
    }

    // delete by Id
    @DeleteMapping("/authors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable int id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    // get author books by Id
    @GetMapping("/authors/{id}/books")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<Book>> getAuthorBooks(@PathVariable int id) {
        return ResponseEntity.ok(authorService.getAuthorBooks(id));
    }
}
