package com.events.application.jwt;

import com.events.application.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    @Autowired
    private UserRepository userRepo;

    private static final String SECRET = "E04B0D4916B6152E70C09B03FCE4FE5F85353C884E280669E76AA70E69EF78207275B0539310CCEF09E11808690ACF03B8FE28C614A5606684BA4972AC19AEF8";

    private static final Long DURATION = TimeUnit.MINUTES.toMillis(45);


    public String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        String email = ((EventUserDetails) userDetails).getEmail();
        Long userId=userRepo.findIdByUsername(userDetails.getUsername());

        claims.put("iss", "https://sughosh-portfolio.vercel.app/");
        claims.put("email", email);
        claims.put("userId", userId.toString());
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(DURATION)))
                .signWith(generateKey())
                .compact();
    }


    private SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }

    public Long extractUserId(String jwt){
        Claims claims= getClaims(jwt);
        String userIdClaim = claims.get("userId", String.class);
        return Long.parseLong(userIdClaim);
    }

    public String extractEmail(String jwt) {
        Claims claims = getClaims(jwt);
        String email= claims.get("email", String.class);
        System.out.println(email);
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email not found in JWT claims");
        }

        return email;
    }


    private Claims getClaims(String jwt) {
        Claims claims= Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims;
    }

    public boolean isTokenValid(String jwt) {
        Claims claims=getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }

}
