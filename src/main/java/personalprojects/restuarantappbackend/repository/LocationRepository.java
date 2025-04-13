package personalprojects.restuarantappbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import personalprojects.restuarantappbackend.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Custom query methods can be defined here if needed
    // For example, findByCity(String city) or findByState(String state)
}
