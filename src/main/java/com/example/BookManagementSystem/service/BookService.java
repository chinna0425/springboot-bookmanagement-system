package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.dto.*;
import com.example.BookManagementSystem.exception.BadRequestException;
import com.example.BookManagementSystem.exception.ResourceNotFoundException;
import com.example.BookManagementSystem.model.Author;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.model.Publisher;
import com.example.BookManagementSystem.repository.AuthorJpaRepository;
import com.example.BookManagementSystem.repository.BookJpaRepository;
import com.example.BookManagementSystem.repository.PublisherJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private PublisherJpaRepository publisherJpaRepository;

    @Autowired
    private AuthorJpaRepository authorJpaRepository;

    public List<BookResponseDto> getBooks() {
        List<Book> books = bookJpaRepository.findAll();
        List<BookResponseDto> result = new ArrayList<>();
        for (Book b : books) {
            result.add(mapBookToDto(b));
        }
        return result;
    }

    public BookResponseDto getBookById(int bookId) {
        Book book = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with id " + bookId));
        return mapBookToDto(book);
    }

    public BookResponseDto addBook(BookCreateRequestDto req) {
        Publisher publisher = publisherJpaRepository.findById(req.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        List<Author> authors = fetchAuthorsByIdsOrThrow(req.getAuthorIds());

        Book book = new Book();
        book.setName(req.getName());
        book.setImageUrl(req.getImageUrl());
        book.setPublisher(publisher);
        book.setAuthorsList(authors);

        Book saved = bookJpaRepository.save(book);

        // minimal bidirectional sync
        for (Author a : authors) {
            a.getBooksList().add(saved);
        }
        authorJpaRepository.saveAll(authors);

        return mapBookToDto(saved);
    }

    public BookResponseDto updateBook(int bookId, BookUpdateRequestDto req) {

        if (req.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        Book book = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (req.getName() != null) {
            book.setName(req.getName());
        }

        if (req.getImageUrl() != null) {
            book.setImageUrl(req.getImageUrl());
        }

        if (req.getPublisherId() != null) {
            Publisher publisher = publisherJpaRepository.findById(req.getPublisherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));
            book.setPublisher(publisher);
        }

        if (req.getAuthorIds() != null) {

            // Removing old mappings
            for (Author a : book.getAuthorsList()) {
                a.getBooksList().remove(book);
            }
            authorJpaRepository.saveAll(book.getAuthorsList());

            // ADDing new mappings
            List<Author> newAuthors = fetchAuthorsByIdsOrThrow(req.getAuthorIds());
            for (Author a : newAuthors) {
                a.getBooksList().add(book);
            }
            authorJpaRepository.saveAll(newAuthors);

            book.setAuthorsList(newAuthors);
        }

        return mapBookToDto(bookJpaRepository.save(book));
    }


    public void deleteBook(int bookId) {
        Book book = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        for (Author a : book.getAuthorsList()) {
            a.getBooksList().remove(book);
        }
        authorJpaRepository.saveAll(book.getAuthorsList());

        bookJpaRepository.delete(book);
    }

    public PublisherResponseDto getBookPublisher(int bookId) {
        Book book = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Publisher p = book.getPublisher();
        if (p == null) {
            throw new ResourceNotFoundException("Publisher not assigned");
        }

        return new PublisherResponseDto(
                p.getPublisherId(),
                p.getPublisherName()
        );
    }

    public List<AuthorResponseDto> getBookAuthors(int bookId) {
        Book book = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        List<AuthorResponseDto> result = new ArrayList<>();
        for (Author a : book.getAuthorsList()) {
            result.add(new AuthorResponseDto(
                    a.getAuthorId(),
                    a.getAuthorName()
            ));
        }
        return result;
    }

    // helper functions

    private List<Author> fetchAuthorsByIdsOrThrow(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Author> authors = authorJpaRepository.findAllById(ids);
        if (authors.size() != ids.size()) {
            throw new BadRequestException("One or more authors not found");
        }
        return authors;
    }

    private BookResponseDto mapBookToDto(Book b) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(b.getId());
        dto.setName(b.getName());
        dto.setImageUrl(b.getImageUrl());

        if (b.getPublisher() != null) {
            dto.setPublisher(new PublisherResponseDto(
                    b.getPublisher().getPublisherId(),
                    b.getPublisher().getPublisherName()
            ));
        }

        List<AuthorResponseDto> authors = new ArrayList<>();
        for (Author a : b.getAuthorsList()) {
            authors.add(new AuthorResponseDto(
                    a.getAuthorId(),
                    a.getAuthorName()
            ));
        }
        dto.setAuthors(authors);

        return dto;
    }
}
