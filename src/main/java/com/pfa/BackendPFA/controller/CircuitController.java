package com.pfa.BackendPFA.controller;


import com.pfa.BackendPFA.entity.Circuit;
import com.pfa.BackendPFA.entity.City;
import com.pfa.BackendPFA.entity.Monument;
import com.pfa.BackendPFA.entity.Tourist;
import com.pfa.BackendPFA.model.CircuitRequest;
import com.pfa.BackendPFA.model.CircuitResponse;
import com.pfa.BackendPFA.repository.CircuitRepository;
import com.pfa.BackendPFA.repository.CityRepository;
import com.pfa.BackendPFA.repository.MonumentRepository;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/circuits")
@CrossOrigin("*")
public class CircuitController {

    private final CircuitRepository circuitRepository;
    private final CityRepository cityRepository;
    private final MonumentRepository monumentRepository;
    private final TouristRepository touristRepository;

    @GetMapping("/{email:.+}")
    public ResponseEntity<List<CircuitResponse>> getAllCircuits(@PathVariable String email) {
        Tourist tourist = touristRepository.findByEmail(email).get();
        List<Circuit> circuits = circuitRepository.findByTourist(tourist);
        List<CircuitResponse> circuitResponses = circuits.stream()
                .map(this::mapToCircuitResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(circuitResponses);
    }

    private CircuitResponse mapToCircuitResponse(Circuit circuit) {
        CircuitResponse response = new CircuitResponse();
        response.setCircuitId(circuit.getId());
        response.setDepartureTime(circuit.getDepartureTime());
        response.setDepartureMonumentName(circuit.getDepartureMonument().getName());

        // Assuming City is associated with Circuit and City has a reference to Monument
        response.setCityName(circuit.getCity().getName());

        return response;
    }

    @PostMapping
    public ResponseEntity<?> createCircuit(@RequestBody CircuitRequest request){
        try {
            System.out.println(request.getMonuments());
            System.out.println(request.getTouristEmail());
            System.out.println(request.getDepDate());
            System.out.println(request.getCityName());
            System.out.println(request.getDepMonument());
            Circuit circuit = new Circuit();
            City city = cityRepository.findByName(request.getCityName());
            List<Monument> monumentList = request.getMonuments()
                    .stream()
                    .map(monumentRepository::findByName)
                    .toList();
            Monument monument = monumentRepository.findByName(request.getDepMonument());
            Date departure = request.getDepDate();
            Optional<Tourist> touristOptional = touristRepository.findByEmail(request.getTouristEmail());
            Tourist tourist = null;
            if (touristOptional.isPresent()){
                tourist = touristOptional.get();
            } else {
                throw new RuntimeException();
            }
            circuit.setCity(city);
            circuit.setMonuments(monumentList);
            circuit.setDepartureMonument(monument);
            circuit.setDepartureTime(departure);
            circuit.setTourist(tourist);
            circuit.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            circuitRepository.save(circuit);
            return ResponseEntity.ok(circuit);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCircuit(@PathVariable int id){
        try {
            Optional<Circuit> circuit = circuitRepository.findById(id);
            if (circuit.isPresent()){
                circuitRepository.deleteById(id);
            } else {
                throw new RuntimeException();
            }

            return ResponseEntity.ok().build();
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/{email}/{circuitID}")
    public ResponseEntity<?> getCircuitById(@PathVariable String email, @PathVariable int circuitID){

        /*
        I am doing this because to calculate the optimal route I need to provide to my algorithm data in this form,
        so I am trying to convert it in the backend, and then returned it as well as the normal circuit data that
        will be needed to display information like city name, monument name etc....

                    {
              "departure":[34.06525, -4.97336],
              "coordinates": [
                [34.06481995939324, -4.978783817720926],
                [34.05156855629086, -4.993323516100645],
                [34.05880269350849,-4.986556495774336]
              ]
            }
         */

        try{
            Optional<Circuit> circuitOptional = circuitRepository.findById(circuitID);
            if (circuitOptional.isPresent()){
                Circuit circuit = circuitOptional.get();
                if (!circuit.getTourist().getEmail().equals(email)){
                    throw new RuntimeException();
                }
                Map<String, Object> calculationRequirements = new HashMap<>();
                calculationRequirements.put("departure", new double[]{
                        circuit.getDepartureMonument().getLatitude(), circuit.getDepartureMonument().getLongitude()
                });
                List<double[]> monumentsCoordinates = new ArrayList<>();
                for (Monument monument:
                        circuit.getMonuments()) {
                    // return just the monuments without the departure
                    if (monument.getId() != circuit.getDepartureMonument().getId()){
                        monumentsCoordinates.add(new double[]{monument.getLatitude(),monument.getLongitude()});
                    }
                }
                calculationRequirements.put("coordinates", monumentsCoordinates);

                Map<String, Object> response = new HashMap<>();
                response.put("circuit", circuit);
                response.put("calculationRequirements", calculationRequirements);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

}
