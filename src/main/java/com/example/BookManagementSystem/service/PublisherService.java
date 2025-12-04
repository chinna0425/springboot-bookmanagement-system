package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.dto.PublisherRequestDto;
import com.example.BookManagementSystem.dto.PublisherResponseDto;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.model.Publisher;
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
import java.util.List;

@Service
@Transactional
public class PublisherService {

    private static final Logger log = LoggerFactory.getLogger(PublisherService.class);

    @Autowired
    private PublisherJpaRepository publisherJpaRepository;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    public List<PublisherResponseDto> getPublishers() {
        List<Publisher> publishers = publisherJpaRepository.findAll();
        List<PublisherResponseDto> result = new ArrayList<>();
        for (Publisher p : publishers) {
            if (p == null) continue;
            PublisherResponseDto dto = new PublisherResponseDto();
            dto.setPublisherId(p.getPublisherId());
            dto.setPublisherName(p.getPublisherName());
            result.add(dto);
        }
        return result;
    }

    public PublisherResponseDto getPublisherById(int publisherId) {
        Publisher p = publisherJpaRepository.findById(publisherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
        PublisherResponseDto dto = new PublisherResponseDto();
        dto.setPublisherId(p.getPublisherId());
        dto.setPublisherName(p.getPublisherName());
        return dto;
    }

    public PublisherResponseDto addPublisher(PublisherRequestDto req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Publisher required");
        }

        String name = req.getPublisherName();
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PublisherName required");
        }

        Publisher p = new Publisher();
        p.setPublisherName(name.trim());

        Publisher saved = publisherJpaRepository.save(p);

        PublisherResponseDto dto = new PublisherResponseDto();
        dto.setPublisherId(saved.getPublisherId());
        dto.setPublisherName(saved.getPublisherName());
        return dto;
    }

    public PublisherResponseDto updatePublisher(int publisherId, PublisherRequestDto req) {
        Publisher existing = publisherJpaRepository.findById(publisherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Publisher payload required");
        }

        if (req.getPublisherName() != null) {
            existing.setPublisherName(req.getPublisherName());
        }

        existing = publisherJpaRepository.save(existing);

        PublisherResponseDto dto = new PublisherResponseDto();
        dto.setPublisherId(existing.getPublisherId());
        dto.setPublisherName(existing.getPublisherName());
        return dto;
    }

    public void deletePublisher(int publisherId) {
        Publisher existing = publisherJpaRepository.findById(publisherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

        List<Book> books = bookJpaRepository.findByPublisher(existing);
        if (books == null) books = new ArrayList<>();

        for (Book b : books) {
            if (b != null) {
                b.setPublisher(null);
            }
        }

        if (!books.isEmpty()) {
            bookJpaRepository.saveAll(books);
        }
        publisherJpaRepository.delete(existing);
    }
}
