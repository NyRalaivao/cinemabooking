package com.cinema.depo.endpoint.security;

import com.cinema.depo.domain.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  // ⚠️ à déplacer en variable d'environnement / AWS SSM avant la prod
  private final Key key =
      Keys.hmacShaKeyFor("change-this-secret-change-this-secret-32b".getBytes());
  private final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24h

  public String generateToken(User user) {
    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("role", user.getRole().name())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
        .signWith(key)
        .compact();
  }

  public UUID extractUserId(String token) {
    String subject =
        Jwts.parser()
            .verifyWith((javax.crypto.SecretKey) key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    return UUID.fromString(subject);
  }

  public boolean isValid(String token) {
    try {
      Jwts.parser().verifyWith((javax.crypto.SecretKey) key).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
