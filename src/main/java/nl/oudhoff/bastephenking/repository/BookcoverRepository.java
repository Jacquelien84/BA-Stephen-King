package nl.oudhoff.bastephenking.repository;

import nl.oudhoff.bastephenking.model.Bookcover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookcoverRepository extends JpaRepository<Bookcover, String> {
}