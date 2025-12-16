package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.*;
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
    @GetMapping("/publisher/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PublisherResponseDto> getPublisherById(@PathVariable int id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }

    // add publisher
    @PostMapping("/addPublisher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherResponseDto> addPublisher(
            @Valid @RequestBody PublisherCreateRequestDto req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publisherService.addPublisher(req));
    }

    // update publisher by id
    @PutMapping("/publisher/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherResponseDto> updatePublisher(
            @PathVariable int id,
            @Valid @RequestBody PublisherUpdateRequestDto req) {

        return ResponseEntity.ok(publisherService.updatePublisher(id, req));
    }

    // delete publisher by id
    @DeleteMapping("/publisher/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePublisher(@PathVariable int id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/publisher/{id}/books")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<PublisherBooksDto>> getAuthors(@PathVariable int id) {
        return ResponseEntity.ok(publisherService.getPublisherBooks(id));
    }
}
