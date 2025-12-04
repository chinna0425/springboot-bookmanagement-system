package com.example.BookManagementSystem.config;

import com.example.BookManagementSystem.service.JwtService;
import com.example.BookManagementSystem.service.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        // ---- 1. Extract token ----
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();

            try {
                userName = jwtService.extractUserName(token);  // may throw exceptions
            } catch (ExpiredJwtException e) {
                System.out.println("JWT EXPIRED: " + e.getMessage());
            } catch (MalformedJwtException | UnsupportedJwtException | SignatureException e) {
                System.out.println("INVALID JWT: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("TOKEN ERROR: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error while parsing token: " + e.getMessage());
            }
        }

        // ---- 2. Validate and authenticate ----
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

            try {
                userDetails = context.getBean(MyUserDetailsService.class)
                        .loadUserByUsername(userName);
            } catch (Exception e) {
                System.out.println("UserDetails loading failed for username: " + userName);
            }

            // ---- 3. Validate Token ----
            if (userDetails != null) {
                boolean isValid = false;
                try {
                    isValid = jwtService.validateToken(token, userDetails);
                } catch (Exception e) {
                    System.out.println("Token validation failed: " + e.getMessage());
                }

                if (isValid) {
                    // Token IS valid → Register authentication in SecurityContext
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Token is INVALID
                    System.out.println("INVALID TOKEN for user: " + userName);
                }
            } else {
                // userDetails is null
                System.out.println("User not found in DB for username: " + userName);
            }
        }

        // ---- 4. Continue filter chain ----
        filterChain.doFilter(request, response);
    }
}
