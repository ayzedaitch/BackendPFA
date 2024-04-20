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
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @GetMapping
    public ResponseEntity<List<CircuitResponse>> getAllCircuits() {
        List<Circuit> circuits = circuitRepository.findAll();
        List<CircuitResponse> circuitResponses = circuits.stream()
                .map(this::mapToCircuitResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(circuitResponses);
    }

    private CircuitResponse mapToCircuitResponse(Circuit circuit) {
        CircuitResponse response = new CircuitResponse();
        response.setCircuitId(circuit.getId());
        response.setDepartureTime(circuit.getDepartureTime());
        response.setDepartureMonumentId(circuit.getDepartureMonument().getId());

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
}
