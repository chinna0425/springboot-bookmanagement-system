package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.AuthorRequestDto;
import com.example.BookManagementSystem.dto.AuthorResponseDto;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthorController {

    private static final Logger log = LoggerFactory.getLogger(AuthorController.class);

    @Autowired
    private AuthorService authorService;

    // Get Method
    @GetMapping("/authors")
    public ResponseEntity<?> getAuthors() {
        try {
            List<AuthorResponseDto> list = authorService.getAuthors();
            return ResponseEntity.ok(list);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Unexpected error while fetching authors", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

    //Get Method
    @GetMapping("/authors/{id}")
    public ResponseEntity<?> getAuthorById(@PathVariable("id") int id) {
        try {
            AuthorResponseDto dto = authorService.getAuthorById(id);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error fetching author id={}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

    // Post Method
    @PostMapping("/authors")
    public ResponseEntity<?> addAuthor(@RequestBody(required = false) AuthorRequestDto req) {
        try {
            if(req==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author details cannot be null");
            }
            if (req.getAuthorName() == null || req.getAuthorName().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author name is required");
            }

            AuthorResponseDto created = authorService.addAuthor(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error creating author", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not create author"));
        }
    }

    //Put Method
    @PutMapping("/authors/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable("id") int id, @RequestBody(required = false) AuthorRequestDto req) {
        try {
            if (req == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author payload required");
            }

            if (req.getAuthorName()==null && (req.getBookIds()==null || req.getBookIds().isEmpty())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "At least one field must be provided to update"
                );
            }
            AuthorResponseDto updated = authorService.updateAuthor(id, req);
            return ResponseEntity.ok(updated);

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error updating author id={}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not update author"));
        }
    }

    //Delete Method
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable("id") int id) {
        try {
            authorService.deleteAuthor(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error deleting author id={}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not delete author"));
        }
    }

    //Get Method
    @GetMapping("/authors/{authorId}/books")
    public ResponseEntity<?> getAuthorBooks(@PathVariable("authorId") int authorId) {
        try {
            List<Book> books = authorService.getAuthorBooks(authorId);
            return ResponseEntity.ok(books);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error fetching books for authorId={}", authorId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }
}
