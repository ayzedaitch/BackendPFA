package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.entity.City;
import com.pfa.BackendPFA.entity.Monument;
import com.pfa.BackendPFA.repository.CityRepository;
import com.pfa.BackendPFA.repository.MonumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monuments")
@RequiredArgsConstructor
public class MonumentsController {
    private final MonumentRepository monumentRepository;
    @GetMapping
    public ResponseEntity<?> getMonumentByCity(){
        try{
            List<Monument> monuments = monumentRepository.findAll();
            return ResponseEntity.ok(monuments);

        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }
}
