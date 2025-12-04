package com.example.BookManagementSystem.controller;

import com.example.BookManagementSystem.dto.PublisherRequestDto;
import com.example.BookManagementSystem.dto.PublisherResponseDto;
import com.example.BookManagementSystem.model.Publisher;
import com.example.BookManagementSystem.service.PublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PublisherController {

    private static final Logger log = LoggerFactory.getLogger(PublisherController.class);

    @Autowired
    private PublisherService publisherService;

    @GetMapping("/publishers")
    public ResponseEntity<?> getPublishers() {
        try {
            List<PublisherResponseDto> list = publisherService.getPublishers();
            return ResponseEntity.ok(list);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Unexpected error while fetching publishers", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

    @GetMapping("/publishers/{publisherId}")
    public ResponseEntity<?> getPublisherById(@PathVariable("publisherId") int publisherId) {
        try {
            PublisherResponseDto dto = publisherService.getPublisherById(publisherId);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error fetching publisher id={}", publisherId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

    @PostMapping("/publishers")
    public ResponseEntity<?> addPublisher(@RequestBody(required = false) PublisherRequestDto req) {
        try {
            if (req == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Publisher cannot be empty"));
            }
            if(req.getPublisherName()==null || req.getPublisherName().isBlank()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "PublisherName required"));
            }
            PublisherResponseDto created = publisherService.addPublisher(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not create publisher"));
        }
    }

    @PutMapping("/publishers/{publisherId}")
    public ResponseEntity<?> updatePublisher(@PathVariable("publisherId") int publisherId,
                                             @RequestBody(required = false) PublisherRequestDto req) {
        try {
            if(req==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Publisher Details cannot be null for update");
            }
            if(req.getPublisherName()==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Atleast provide one filed to update");
            }
            PublisherResponseDto updated = publisherService.updatePublisher(publisherId, req);
            return ResponseEntity.ok(updated);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error updating publisher id={}", publisherId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not update publisher"));
        }
    }

    @DeleteMapping("/publishers/{publisherId}")
    public ResponseEntity<?> deletePublisher(@PathVariable("publisherId") int publisherId) {
        try {
            publisherService.deletePublisher(publisherId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            log.error("Error deleting publisher id={}", publisherId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not delete publisher"));
        }
    }
}
