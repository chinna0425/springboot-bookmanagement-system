package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.PublisherCreateRequestDto;
import com.example.BookManagementSystem.dto.PublisherResponseDto;
import com.example.BookManagementSystem.dto.PublisherUpdateRequestDto;
import com.example.BookManagementSystem.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    // get all publishers
    @GetMapping("/publishers")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<PublisherResponseDto>> getPublishers() {
        return ResponseEntity.ok(publisherService.getPublishers());
    }

    // get publisher by id
    @GetMapping("/publishers/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PublisherResponseDto> getPublisherById(@PathVariable int id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }

    // add publisher
    @PostMapping("/publishers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherResponseDto> addPublisher(
            @Valid @RequestBody PublisherCreateRequestDto req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publisherService.addPublisher(req));
    }

    // update publisher by id
    @PutMapping("/publishers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherResponseDto> updatePublisher(
            @PathVariable int id,
            @Valid @RequestBody PublisherUpdateRequestDto req) {

        return ResponseEntity.ok(publisherService.updatePublisher(id, req));
    }

    // delete publisher by id
    @DeleteMapping("/publishers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePublisher(@PathVariable int id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
