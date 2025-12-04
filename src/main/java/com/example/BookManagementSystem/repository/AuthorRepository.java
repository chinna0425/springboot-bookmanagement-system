package com.example.BookManagementSystem.repository;


import com.example.BookManagementSystem.model.Author;
import com.example.BookManagementSystem.model.Book;

import java.util.ArrayList;
import java.util.List;

public interface AuthorRepository {
    ArrayList<Author> getAuthors();

    Author getAuthorById(int authorId);

    Author addAuthor(Author author);

    Author updateAuthor(int authorId, Author author);

    void deleteAuthor(int authorId);

    List<Book> getAuthorBooks(int authorId);
}

