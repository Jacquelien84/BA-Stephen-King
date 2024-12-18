package nl.oudhoff.bastephenking.repository;

import nl.oudhoff.bastephenking.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("select t from Token t where t.user.username = :username and t.loggedOut = false")
    List<Token> findAllAccessTokensByUser(String username);

    Optional<Token> findByAccessToken(String token);

    Optional<Token> findByRefreshToken(String token);
}
