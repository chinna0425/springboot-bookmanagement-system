package com.example.BookManagementSystem.service;

import com.example.BookManagementSystem.model.Users;
import com.example.BookManagementSystem.model.UserDetailsImpl;
import com.example.BookManagementSystem.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
        import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (identifier == null) throw new UsernameNotFoundException("User identifier is null");

        // normalize
        String id = identifier.trim();

        Optional<Users> opt = userJpaRepository.findByEmail(id);
        if (opt.isEmpty()) opt = userJpaRepository.findByUserName(id); // requires method in repo

        Users user = opt.orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
        return new UserDetailsImpl(user);
    }
}
