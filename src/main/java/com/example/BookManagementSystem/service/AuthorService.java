package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.dto.AuthorCreateRequestDto;
import com.example.BookManagementSystem.dto.AuthorResponseDto;
import com.example.BookManagementSystem.dto.AuthorUpdateRequestDto;
import com.example.BookManagementSystem.exception.BadRequestException;
import com.example.BookManagementSystem.exception.ResourceNotFoundException;
import com.example.BookManagementSystem.model.Author;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.repository.AuthorJpaRepository;
import com.example.BookManagementSystem.repository.BookJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AuthorService {

    @Autowired
    private AuthorJpaRepository authorJpaRepository;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    public List<AuthorResponseDto> getAuthors() {
        List<Author> authors = authorJpaRepository.findAll();
        List<AuthorResponseDto> result = new ArrayList<>();

        for (Author a : authors) {
            result.add(mapToDto(a));
        }
        return result;
    }

    public AuthorResponseDto getAuthorById(int authorId) {
        Author author = authorJpaRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        return mapToDto(author);
    }

    public AuthorResponseDto addAuthor(AuthorCreateRequestDto req) {

        Author author = new Author();
        author.setAuthorName(req.getAuthorName());

        Author saved = authorJpaRepository.save(author);

        if (req.getBookIds() != null && !req.getBookIds().isEmpty()) {
            List<Book> books = fetchBooksByIdsOrThrow(req.getBookIds());

            for (Book b : books) {
                b.getAuthorsList().add(saved); // bidirectional
            }
            bookJpaRepository.saveAll(books);
            saved.setBooksList(books);
        }

        return mapToDto(saved);
    }

    public AuthorResponseDto updateAuthor(int authorId, AuthorUpdateRequestDto req) {

        if (req.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        Author author = authorJpaRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        if (req.getAuthorName() != null) {
            author.setAuthorName(req.getAuthorName());
        }

        if (req.getBookIds() != null) {

            // detach old
            for (Book b : author.getBooksList()) {
                b.getAuthorsList().remove(author);
            }
            bookJpaRepository.saveAll(author.getBooksList());

            // attach new
            List<Book> newBooks = fetchBooksByIdsOrThrow(req.getBookIds());
            for (Book b : newBooks) {
                b.getAuthorsList().add(author);
            }
            bookJpaRepository.saveAll(newBooks);

            author.setBooksList(newBooks);
        }

        return mapToDto(authorJpaRepository.save(author));
    }

    public void deleteAuthor(int authorId) {
        Author author = authorJpaRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        for (Book b : author.getBooksList()) {
            b.getAuthorsList().remove(author);
        }
        bookJpaRepository.saveAll(author.getBooksList());

        authorJpaRepository.delete(author);
    }

    public List<Book> getAuthorBooks(int authorId) {
        Author author = authorJpaRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        return author.getBooksList();
    }

    // helper functions

    private List<Book> fetchBooksByIdsOrThrow(List<Integer> ids) {
        List<Book> books = bookJpaRepository.findAllById(ids);
        if (books.size() != ids.size()) {
            throw new BadRequestException("One or more books not found");
        }
        return books;
    }

    private AuthorResponseDto mapToDto(Author a) {
        return new AuthorResponseDto(a.getAuthorId(), a.getAuthorName());
    }
}
