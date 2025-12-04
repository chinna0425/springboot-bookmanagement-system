package com.example.BookManagementSystem.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookid")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "imageurl")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "publisherid")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(
            name="book_author",
            joinColumns=@JoinColumn(name="bookid"),
            inverseJoinColumns=@JoinColumn(name="authorid")
    )
    @JsonIgnoreProperties("booksList")
    private List<Author> authorsList=new ArrayList<>();
}
