package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.*;
import com.example.BookManagementSystem.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    // get all books
    @GetMapping("/books")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<BookResponseDto>> getBooks() {
        return ResponseEntity.ok(bookService.getBooks());
    }

    // get book by Id
    @GetMapping("/books/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable int id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // add book
    @PostMapping("/addBook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> addBook(
            @Valid @RequestBody BookCreateRequestDto req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.addBook(req));
    }

    // update book by id
    @PutMapping("/book/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable int id,
            @Valid @RequestBody BookUpdateRequestDto req) {

        return ResponseEntity.ok(bookService.updateBook(id, req));
    }

    // delete book by id
    @DeleteMapping("/book/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // get publishers by book id
    @GetMapping("/book/{id}/publisher")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PublisherResponseDto> getPublisher(@PathVariable int id) {
        return ResponseEntity.ok(bookService.getBookPublisher(id));
    }

    // get authors by book id
    @GetMapping("/book/{id}/authors")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<AuthorResponseDto>> getAuthors(@PathVariable int id) {
        return ResponseEntity.ok(bookService.getBookAuthors(id));
    }
}
