package com.example.BookManagementSystem.repository;

import com.example.BookManagementSystem.model.Author;
import com.example.BookManagementSystem.model.Book;
import com.example.BookManagementSystem.model.Publisher;

import java.util.ArrayList;
import java.util.List;

public interface BookRepository {
    ArrayList<Book> getBooks();

    Book getBookById(int bookId);

    Book addBook(Book book);

    Book updateBook(int bookId, Book book);

    void deleteBook(int bookId);

    Publisher getBookPublisher(int bookId);

    List<Author> getBookAuthors(int bookId);
}
