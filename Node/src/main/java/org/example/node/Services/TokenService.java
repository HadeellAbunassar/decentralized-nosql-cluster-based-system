package org.example.node.Services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.node.Models.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(User user) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(user.getUserName())
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + 3600000)) // 1 hour expiration
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            String subject = claims.getBody().getSubject();

            if (!username.equals(subject)) {
                return false;
            }

            Date expirationDate = claims.getBody().getExpiration();
            boolean isExpired = expirationDate != null && expirationDate.before(new Date()); // check if the user session expired

            // If no exceptions are thrown and token is not expired, return true
            return !isExpired;

        } catch (ExpiredJwtException e) {
            // Token has expired
            return false;
        } catch (Exception e) {
            return false;
        }
    }




}


