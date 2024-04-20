package com.pfa.BackendPFA.controller;


import com.pfa.BackendPFA.entity.Circuit;
import com.pfa.BackendPFA.model.CircuitResponse;
import com.pfa.BackendPFA.repository.CircuitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/circuits")
@CrossOrigin("*")
public class CircuitController {

    @Autowired
    private CircuitRepository circuitRepository;

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
}
