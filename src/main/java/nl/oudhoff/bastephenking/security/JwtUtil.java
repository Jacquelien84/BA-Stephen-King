package nl.oudhoff.bastephenking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Setter;
import nl.oudhoff.bastephenking.model.User;
import nl.oudhoff.bastephenking.repository.TokenRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static nl.oudhoff.bastephenking.util.RandomStringGenerator.getGeneratedString;

@Service
public class JwtUtil {

    private final static String SECRET_KEY = getGeneratedString();

    @Setter
    private long refreshTokenExpire = 1000 * 60 * 60 * 24 * 10; // 10 days;

    private final TokenRepository tokenRepo;

    public JwtUtil(TokenRepository tokenRepository) {
        this.tokenRepo = tokenRepository;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);

        // Voeg extra logging toe om de validatie te debuggen
        System.out.println("Username in token: " + username);
        System.out.println("Username in UserDetails: " + user.getUsername());
        System.out.println("Is token expired: " + isTokenExpired(token));

        boolean validToken = tokenRepo
                .findByAccessToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        System.out.println("Is token valid in repo: " + validToken);

        return (username.equals(user.getUsername())) && !isTokenExpired(token) && validToken;
    }


    public boolean isValidRefreshToken(String token, User user) {
        String username = extractUsername(token);

        boolean validRefreshToken = tokenRepo
                .findByRefreshToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        return (username.equals(user.getUsername())) && !isTokenExpired(token) && validRefreshToken;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }


    public String generateAccessToken(User user) {
        long accessTokenExpire = 1000 * 60 * 60 * 24 * 10; // 10 day in ms;
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, user.getUsername(), accessTokenExpire);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, user.getUsername(), refreshTokenExpire);
    }

    private String generateToken(Map<String, Object> claims, String subject, Long expireTime) {
        return Jwts.builder().setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }
}
