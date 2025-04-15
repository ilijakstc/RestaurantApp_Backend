package personalprojects.restuarantappbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personalprojects.restuarantappbackend.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
