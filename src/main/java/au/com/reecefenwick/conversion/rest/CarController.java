package au.com.reecefenwick.conversion.rest;

import au.com.reecefenwick.conversion.domain.Car;
import au.com.reecefenwick.conversion.repository.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Car.
 */
@RestController
@RequestMapping("/api")
public class CarController {

    private final Logger log = LoggerFactory.getLogger(CarController.class);

    @Autowired
    private CarRepository carRepository;

    /**
     * POST  /cars -> Create a new car.
     */
    @RequestMapping(value = "/cars",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Car> create(@RequestBody Car car) throws URISyntaxException {
        log.debug("REST request to save Car : {}", car);
        if (car.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new car cannot already have an ID").body(null);
        }
        Car result = carRepository.save(car);
        return ResponseEntity.created(new URI("/api/cars/" + result.getId()))
                .body(result);
    }

    /**
     * PUT  /cars -> Updates an existing car.
     */
    @RequestMapping(value = "/cars",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Car> update(@RequestBody Car car) throws URISyntaxException {
        log.debug("REST request to update Car : {}", car);
        if (car.getId() == null) {
            return create(car);
        }
        Car result = carRepository.save(car);
        return ResponseEntity.ok()
                .body(result);
    }

    /**
     * GET  /cars -> get all the cars.
     */
    @RequestMapping(value = "/cars",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Car> getAll() {
        log.debug("REST request to get all Cars");
        return carRepository.findAll();
    }

    /**
     * GET  /cars/:id -> get the "id" car.
     */
    @RequestMapping(value = "/cars/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Car> get(@PathVariable Long id) {
        log.debug("REST request to get Car : {}", id);
        return Optional.ofNullable(carRepository.findOne(id))
                .map(car -> new ResponseEntity<>(
                        car,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /cars/:id -> delete the "id" car.
     */
    @RequestMapping(value = "/cars/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Car : {}", id);
        carRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
