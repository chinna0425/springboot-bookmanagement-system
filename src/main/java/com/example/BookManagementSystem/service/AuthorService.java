package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.dto.AuthorRequestDto;
import com.example.BookManagementSystem.dto.AuthorResponseDto;
import com.example.BookManagementSystem.model.Author;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.repository.AuthorJpaRepository;
import com.example.BookManagementSystem.repository.BookJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    @Autowired
    private AuthorJpaRepository authorJpaRepository;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    public List<AuthorResponseDto> getAuthors() {
        List<Author> authors = authorJpaRepository.findAll();
        List<AuthorResponseDto> result = new ArrayList<>();
        for (Author a : authors) {
            if (a == null) continue;
            AuthorResponseDto dto = new AuthorResponseDto();
            dto.setAuthorId(a.getAuthorId());
            dto.setAuthorName(a.getAuthorName());
            result.add(dto);
        }
        return result;
    }

    public AuthorResponseDto getAuthorById(int authorId) {
        Author author = authorJpaRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        AuthorResponseDto dto = new AuthorResponseDto();
        dto.setAuthorId(author.getAuthorId());
        dto.setAuthorName(author.getAuthorName());
        return dto;
    }

    public AuthorResponseDto addAuthor(AuthorRequestDto req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author required");
        }

        // create entity without books
        Author toSave = new Author();
        toSave.setAuthorName(req.getAuthorName());

        // save to get id
        Author saved = authorJpaRepository.save(toSave);

        // if no bookIds provided, return
        List<Integer> bookIds = req.getBookIds();
        if (bookIds == null || bookIds.isEmpty()) {
            AuthorResponseDto resp = new AuthorResponseDto();
            resp.setAuthorId(saved.getAuthorId());
            resp.setAuthorName(saved.getAuthorName());
            return resp;
        }

        // fetch books by ids
        List<Book> foundBooks = fetchBooksByIdsOrThrow(bookIds);

        // attach saved author to each book
        for (Book b : foundBooks) {
            if (b.getAuthorsList() == null) {
                b.setAuthorsList(new ArrayList<>());
            }
            boolean exists = false;
            for (Author a : b.getAuthorsList()) {
                if (a != null && a.getAuthorId() != null && a.getAuthorId().equals(saved.getAuthorId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                b.getAuthorsList().add(saved);
            }
        }

        // save books and update author's booksList
        bookJpaRepository.saveAll(foundBooks);
        saved.setBooksList(foundBooks);
        saved = authorJpaRepository.save(saved);

        AuthorResponseDto resp = new AuthorResponseDto();
        resp.setAuthorId(saved.getAuthorId());
        resp.setAuthorName(saved.getAuthorName());
        return resp;
    }

    public AuthorResponseDto updateAuthor(int authorId, AuthorRequestDto req) {
        Author existing = authorJpaRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author payload required");
        }

        if (req.getAuthorName() != null) {
            existing.setAuthorName(req.getAuthorName());
        }

        if (req.getBookIds() != null) {
            // detach from old books
            List<Book> oldBooks = existing.getBooksList();
            if (oldBooks == null) oldBooks = new ArrayList<>();
            if (!oldBooks.isEmpty()) {
                for (Book b : oldBooks) {
                    if (b != null && b.getAuthorsList() != null) {
                        List<Author> authors = b.getAuthorsList();
                        for (int i = authors.size() - 1; i >= 0; i--) {
                            Author a = authors.get(i);
                            if (a != null && a.getAuthorId() != null && a.getAuthorId().equals(existing.getAuthorId())) {
                                authors.remove(i);
                            }
                        }
                    }
                }
                bookJpaRepository.saveAll(oldBooks);
            }

            // fetch new books
            List<Integer> newIds = req.getBookIds();
            List<Book> newBooks = new ArrayList<>();
            if (newIds != null && !newIds.isEmpty()) {
                newBooks = fetchBooksByIdsOrThrow(newIds);
            }

            // attach existing author to new books
            for (Book b : newBooks) {
                if (b.getAuthorsList() == null) {
                    b.setAuthorsList(new ArrayList<>());
                }
                boolean exists = false;
                for (Author a : b.getAuthorsList()) {
                    if (a != null && a.getAuthorId() != null && a.getAuthorId().equals(existing.getAuthorId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    b.getAuthorsList().add(existing);
                }
            }

            if (!newBooks.isEmpty()) {
                bookJpaRepository.saveAll(newBooks);
            }

            existing.setBooksList(newBooks);
        }

        existing = authorJpaRepository.save(existing);

        AuthorResponseDto resp = new AuthorResponseDto();
        resp.setAuthorId(existing.getAuthorId());
        resp.setAuthorName(existing.getAuthorName());
        return resp;
    }

    public void deleteAuthor(int authorId) {
        Author author = authorJpaRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

        List<Book> books = author.getBooksList();
        if (books == null) books = new ArrayList<>();

        for (Book b : books) {
            if (b != null && b.getAuthorsList() != null) {
                List<Author> authors = b.getAuthorsList();
                for (int i = authors.size() - 1; i >= 0; i--) {
                    Author a = authors.get(i);
                    if (a != null && a.getAuthorId() != null && a.getAuthorId().equals(author.getAuthorId())) {
                        authors.remove(i);
                    }
                }
            }
        }

        if (!books.isEmpty()) {
            bookJpaRepository.saveAll(books);
        }

        authorJpaRepository.delete(author);
    }

    public List<Book> getAuthorBooks(int authorId) {
        Author author = authorJpaRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        List<Book> result = author.getBooksList();
        if (result == null) return new ArrayList<>();
        return result;
    }

    //helper function
    private List<Book> fetchBooksByIdsOrThrow(List<Integer> ids) {
        if (ids == null) return new ArrayList<>();

        List<Book> found = bookJpaRepository.findAllById(ids);

        // compute unique requested ids
        Set<Integer> uniqueRequested = new HashSet<>();
        for (Integer id : ids) {
            if (id != null) uniqueRequested.add(id);
        }

        // compute found ids
        Set<Integer> foundIds = new HashSet<>();
        for (Book b : found) {
            if (b != null && b.getId() != null) foundIds.add(b.getId());
        }

        // check missing
        List<Integer> missing = new ArrayList<>();
        for (Integer id : uniqueRequested) {
            if (!foundIds.contains(id)) missing.add(id);
        }

        if (!missing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some books not found: " + missing);
        }
        return found;
    }
}
