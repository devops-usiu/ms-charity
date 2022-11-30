package co.ke.collengine.springbootapiexample.controller;

import co.ke.collengine.springbootapiexample.entity.CarParkDetails;
import co.ke.collengine.springbootapiexample.entity.CarParkDetailsBeta;
import co.ke.collengine.springbootapiexample.repository.CarParkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/car_park")
public class CarParkController {
    private final CarParkRepository carParkRepository;

    public CarParkController(CarParkRepository themeParkRideRepository) {
        this.carParkRepository = themeParkRideRepository;
    }

    @GetMapping(value = "/ride", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<CarParkDetails> getRides() {
        return carParkRepository.findAll();
    }

    @GetMapping(value = "/ride/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CarParkDetails getRide(@PathVariable long id){
        return carParkRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Invalid ride id %s", id)));
    }

    @PostMapping(value = "/ride", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CarParkDetails createRide(@Valid @RequestBody CarParkDetails themeParkRide) {
        return carParkRepository.save(themeParkRide);
    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CarParkDetails verifyRide(@Valid @RequestBody CarParkDetails themeParkRide) {
        return new CarParkDetails("debit server", "LAX", 616453434, 616453434, "blue");
    }
    @PostMapping(value = "/additional_first", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CarParkDetails additionalFirst(@Valid @RequestBody CarParkDetails themeParkRide) {
        return new CarParkDetails("debit server", "LAX", 616453434, 616453434, "blue");
    }

    @PostMapping(value = "/additional_second", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CarParkDetailsBeta additionalSecond(@Valid @RequestBody CarParkDetailsBeta themeParkRide) {
        return new CarParkDetailsBeta("yaya-center", 616453434);
    }

    @PostMapping(value = "/additional_third", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CarParkDetailsBeta additionalThird(@Valid @RequestBody CarParkDetailsBeta themeParkRide) {
        return new CarParkDetailsBeta("yaya-center", 616453434);
    }
}
