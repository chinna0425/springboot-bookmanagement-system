package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.BookRequestDto;
import com.example.BookManagementSystem.dto.BookResponseDto;
import com.example.BookManagementSystem.dto.AuthorResponseDto;
import com.example.BookManagementSystem.dto.PublisherResponseDto;
import com.example.BookManagementSystem.service.BookService;
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
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<?> getBooks() {
        try {
            List<BookResponseDto> list = bookService.getBooks();
            return ResponseEntity.ok(list);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Unexpected error while fetching books", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable("bookId") int bookId) {
        try {
            BookResponseDto dto = bookService.getBookById(bookId);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error fetching book id={}", bookId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

    @PostMapping("/books")
    public ResponseEntity<?> addBook(@RequestBody(required = false) BookRequestDto req) {
        try {
            if(req==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book Details are not null");
            }
            if (req.getName() == null || req.getName().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book name is required");
            }
            if (req.getPublisherId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Publisher ID is required");
            }

            BookResponseDto created = bookService.addBook(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error creating book", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not create book"));
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable("id") int id,
                                        @RequestBody(required = false) BookRequestDto req) {
        try {
            if (req == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book payload required");
            }
            if(req.getName()==null && req.getImageUrl()==null && req.getPublisherId()==null && (req.getAuthorIds()==null || req.getAuthorIds().isEmpty())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide atleast one field to update");
            }
            BookResponseDto updated = bookService.updateBook(id, req);
            return ResponseEntity.ok(updated);

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error updating book id={}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not update book"));
        }
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable("bookId") int bookId) {
        try {
            bookService.deleteBook(bookId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error deleting book id={}", bookId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not delete book"));
        }
    }

    @GetMapping("/books/{bookId}/publisher")
    public ResponseEntity<?> getBookPublisher(@PathVariable("bookId") int bookId) {
        try {
            PublisherResponseDto pub = bookService.getBookPublisher(bookId);
            return ResponseEntity.ok(pub);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error fetching publisher for bookId={}", bookId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

    @GetMapping("/books/{bookId}/authors")
    public ResponseEntity<?> getBookAuthors(@PathVariable("bookId") int bookId) {
        try {
            List<AuthorResponseDto> authors = bookService.getBookAuthors(bookId);
            return ResponseEntity.ok(authors);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error fetching authors for bookId={}", bookId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }
}
