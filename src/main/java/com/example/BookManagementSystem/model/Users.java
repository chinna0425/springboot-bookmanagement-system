package com.example.BookManagementSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,unique = true)
    private Long id;

    @Column(name = "username", nullable = false,unique = true,length = 100)
    private String userName;

    @Column(name = "email", nullable = false,unique = true,length = 100)
    private String email;

    @Column(name = "passwordhash")
    private String passwordHash;

    @Column(name="createdat")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreatedAt(){
        this.createdAt=LocalDateTime.now();
    }

    public Users(String userName, String email, String passwordHash) {
        this.userName = userName;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
