package com.example.BookManagementSystem.repository;


import com.example.BookManagementSystem.model.Publisher;

import java.util.ArrayList;

public interface PublisherRepository {
    ArrayList<Publisher> getPublishers();

    Publisher getPublisherById(int publisherId);

    Publisher addPublisher(Publisher publisher);

    Publisher updatePublisher(int publisherId, Publisher publisher);

    void deletePublisher(int publisherId);
}
