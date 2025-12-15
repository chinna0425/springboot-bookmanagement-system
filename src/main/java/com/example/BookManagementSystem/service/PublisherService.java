package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.dto.PublisherCreateRequestDto;
import com.example.BookManagementSystem.dto.PublisherResponseDto;
import com.example.BookManagementSystem.dto.PublisherUpdateRequestDto;
import com.example.BookManagementSystem.exception.ResourceNotFoundException;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.model.Publisher;
import com.example.BookManagementSystem.repository.BookJpaRepository;
import com.example.BookManagementSystem.repository.PublisherJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PublisherService {

    @Autowired
    private PublisherJpaRepository publisherJpaRepository;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    public List<PublisherResponseDto> getPublishers() {
        List<Publisher> publishers = publisherJpaRepository.findAll();
        List<PublisherResponseDto> result = new ArrayList<>();

        for (Publisher p : publishers) {
            result.add(mapToDto(p));
        }
        return result;
    }

    public PublisherResponseDto getPublisherById(int publisherId) {
        Publisher p = publisherJpaRepository.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));
        return mapToDto(p);
    }

    public PublisherResponseDto addPublisher(PublisherCreateRequestDto req) {

        Publisher p = new Publisher();
        p.setPublisherName(req.getPublisherName().trim());

        return mapToDto(publisherJpaRepository.save(p));
    }

    public PublisherResponseDto updatePublisher(int publisherId, PublisherUpdateRequestDto req) {

        if (req.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        Publisher existing = publisherJpaRepository.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        if (req.getPublisherName() != null) {
            existing.setPublisherName(req.getPublisherName().trim());
        }

        return mapToDto(publisherJpaRepository.save(existing));
    }

    public void deletePublisher(int publisherId) {

        Publisher existing = publisherJpaRepository.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        List<Book> books = bookJpaRepository.findByPublisher(existing);

        for (Book b : books) {
            b.setPublisher(null); // detach relationship
        }

        if (!books.isEmpty()) {
            bookJpaRepository.saveAll(books);
        }

        publisherJpaRepository.delete(existing);
    }

    // helper functions

    private PublisherResponseDto mapToDto(Publisher p) {
        return new PublisherResponseDto(
                p.getPublisherId(),
                p.getPublisherName()
        );
    }
}
