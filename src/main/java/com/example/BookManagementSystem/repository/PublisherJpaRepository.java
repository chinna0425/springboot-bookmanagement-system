package com.example.BookManagementSystem.repository;

import com.example.BookManagementSystem.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherJpaRepository extends JpaRepository<Publisher,Integer> {
}
