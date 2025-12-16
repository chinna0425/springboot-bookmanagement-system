package com.example.BookManagementSystem.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKeyValue;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ---------------- TOKEN GENERATION ----------------

    public String generateJwtToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles",userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        // here claims is the data in the payload (empty for now)
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // 30 minutes
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ---------------- SECRET KEY GENERATION ----------------

    /* public String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error during generating secret key", e);
        }
    } */

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyValue);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ---------------- EXTRACTION UTILITIES ----------------

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token){
        Claims claims=extractAllClaims(token);
        return claims.get("roles",List.class);
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        if (claims == null) return null;
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null; // keep same behavior as your original code
        }
    }

    // ---------------- VALIDATION ----------------

    public boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        if (claims == null) return false; // invalid token
        String userName = claims.getSubject();
        if (userName == null) return false;
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date exp = extractExpiration(token);
        if (exp == null) return true;
        return exp.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
