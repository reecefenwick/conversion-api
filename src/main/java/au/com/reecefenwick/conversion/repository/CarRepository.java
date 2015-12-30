package au.com.reecefenwick.conversion.repository;

import au.com.reecefenwick.conversion.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Car entity.
 */
public interface CarRepository extends JpaRepository<Car, Long> {

}
