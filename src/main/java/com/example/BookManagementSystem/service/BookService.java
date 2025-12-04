package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.dto.AuthorResponseDto;
import com.example.BookManagementSystem.dto.BookRequestDto;
import com.example.BookManagementSystem.dto.BookResponseDto;
import com.example.BookManagementSystem.dto.PublisherResponseDto;
import com.example.BookManagementSystem.model.Author;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.model.Publisher;
import com.example.BookManagementSystem.repository.AuthorJpaRepository;
import com.example.BookManagementSystem.repository.BookJpaRepository;
import com.example.BookManagementSystem.repository.PublisherJpaRepository;
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
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

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
            if (b == null) continue;
            BookResponseDto dto = mapBookToDto(b);
            result.add(dto);
        }
        return result;
    }

    public BookResponseDto getBookById(int bookId) {
        Book book = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return mapBookToDto(book);
    }

    public BookResponseDto addBook(BookRequestDto req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book required");
        }

        // validate publisher if provided
        Publisher publisher = null;
        if (req.getPublisherId() != null) {
            publisher = publisherJpaRepository.findById(req.getPublisherId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wrong publisherId"));
        }

        // validate authors if provided
        List<Integer> authorIds = req.getAuthorIds();
        List<Author> authors = new ArrayList<>();
        if (authorIds != null && !authorIds.isEmpty()) {
            authors = fetchAuthorsByIdsOrThrow(authorIds);
        }

        // create book entity
        Book toSave = new Book();
        toSave.setName(req.getName());
        toSave.setImageUrl(req.getImageUrl());
        toSave.setPublisher(publisher);
        toSave.setAuthorsList(authors);

        // save book (owning side)
        Book saved = bookJpaRepository.save(toSave);

        // ensure bidirectional relation: add saved book to each author's booksList
        if (saved.getAuthorsList() != null && !saved.getAuthorsList().isEmpty()) {
            for (Author a : saved.getAuthorsList()) {
                if (a.getBooksList() == null) a.setBooksList(new ArrayList<>());
                boolean exists = false;
                for (Book bb : a.getBooksList()) {
                    if (bb != null && bb.getId() != null && bb.getId().equals(saved.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    a.getBooksList().add(saved);
                }
            }
            authorJpaRepository.saveAll(saved.getAuthorsList());
        }

        BookResponseDto resp = mapBookToDto(saved);
        return resp;
    }

    public BookResponseDto updateBook(int bookId, BookRequestDto req) {
        Book existing = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book payload required");
        }

        if (req.getName() != null) existing.setName(req.getName());
        if (req.getImageUrl() != null) existing.setImageUrl(req.getImageUrl());

        // handle publisher change
        if (req.getPublisherId() != null) {
            Publisher p = publisherJpaRepository.findById(req.getPublisherId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
            existing.setPublisher(p);
        }

        // handle authors replacement if provided
        if (req.getAuthorIds() != null) {
            // detach from old authors
            List<Author> oldAuthors = existing.getAuthorsList();
            if (oldAuthors == null) oldAuthors = new ArrayList<>();
            if (!oldAuthors.isEmpty()) {
                for (Author a : oldAuthors) {
                    if (a != null && a.getBooksList() != null) {
                        for (int i = a.getBooksList().size() - 1; i >= 0; i--) {
                            Book bb = a.getBooksList().get(i);
                            if (bb != null && bb.getId() != null && bb.getId().equals(existing.getId())) {
                                a.getBooksList().remove(i);
                            }
                        }
                    }
                }
                authorJpaRepository.saveAll(oldAuthors);
            }

            // fetch new authors
            List<Integer> newAuthorIds = req.getAuthorIds();
            List<Author> newAuthors = new ArrayList<>();
            if (newAuthorIds != null && !newAuthorIds.isEmpty()) {
                newAuthors = fetchAuthorsByIdsOrThrow(newAuthorIds);
            }

            // attach existing book to each new author
            for (Author a : newAuthors) {
                if (a.getBooksList() == null) a.setBooksList(new ArrayList<>());
                boolean exists = false;
                for (Book bb : a.getBooksList()) {
                    if (bb != null && bb.getId() != null && bb.getId().equals(existing.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    a.getBooksList().add(existing);
                }
            }
            if (!newAuthors.isEmpty()) {
                authorJpaRepository.saveAll(newAuthors);
            }
            existing.setAuthorsList(newAuthors);
        }

        existing = bookJpaRepository.save(existing);
        return mapBookToDto(existing);
    }

    public void deleteBook(int bookId) {
        Book book = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        List<Author> authors = book.getAuthorsList();
        if (authors != null && !authors.isEmpty()) {
            for (Author a : authors) {
                if (a != null && a.getBooksList() != null) {
                    for (int i = a.getBooksList().size() - 1; i >= 0; i--) {
                        Book bb = a.getBooksList().get(i);
                        if (bb != null && bb.getId() != null && bb.getId().equals(book.getId())) {
                            a.getBooksList().remove(i);
                        }
                    }
                }
            }
            authorJpaRepository.saveAll(authors);
        }

        bookJpaRepository.deleteById(bookId);
    }

    public PublisherResponseDto getBookPublisher(int bookId) {
        Book existing = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        Publisher p = existing.getPublisher();
        if (p == null) return null;
        PublisherResponseDto dto = new PublisherResponseDto();
        dto.setPublisherId(p.getPublisherId());
        dto.setPublisherName(p.getPublisherName());
        return dto;
    }

    public List<AuthorResponseDto> getBookAuthors(int bookId) {
        Book existing = bookJpaRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        List<Author> authors = existing.getAuthorsList();
        List<AuthorResponseDto> result = new ArrayList<>();
        if (authors == null) return result;
        for (Author a : authors) {
            if (a == null) continue;
            AuthorResponseDto dto = new AuthorResponseDto();
            dto.setAuthorId(a.getAuthorId());
            dto.setAuthorName(a.getAuthorName());
            result.add(dto);
        }
        return result;
    }

    /* ----------------- helpers (no streams) ----------------- */

    private List<Author> fetchAuthorsByIdsOrThrow(List<Integer> ids) {
        List<Author> found = authorJpaRepository.findAllById(ids);

        // build unique requested ids
        Set<Integer> uniqueRequested = new HashSet<>();
        for (Integer id : ids) {
            if (id != null) uniqueRequested.add(id);
        }

        // build found ids set
        Set<Integer> foundIds = new HashSet<>();
        for (Author a : found) {
            if (a != null && a.getAuthorId() != null) foundIds.add(a.getAuthorId());
        }

        // compute missing
        List<Integer> missing = new ArrayList<>();
        for (Integer id : uniqueRequested) {
            if (!foundIds.contains(id)) missing.add(id);
        }

        if (!missing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some authors not found: " + missing);
        }
        return found;
    }

    private BookResponseDto mapBookToDto(Book b) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(b.getId());
        dto.setName(b.getName());
        dto.setImageUrl(b.getImageUrl());

        Publisher p = b.getPublisher();
        if (p != null) {
            PublisherResponseDto pdto = new PublisherResponseDto();
            pdto.setPublisherId(p.getPublisherId());
            pdto.setPublisherName(p.getPublisherName());
            dto.setPublisher(pdto);
        } else {
            dto.setPublisher(null);
        }

        List<Author> authors = b.getAuthorsList();
        List<AuthorResponseDto> authorDtos = new ArrayList<>();
        if (authors != null) {
            for (Author a : authors) {
                if (a == null) continue;
                AuthorResponseDto adto = new AuthorResponseDto();
                adto.setAuthorId(a.getAuthorId());
                adto.setAuthorName(a.getAuthorName());
                authorDtos.add(adto);
            }
        }
        dto.setAuthors(authorDtos);

        return dto;
    }
}
