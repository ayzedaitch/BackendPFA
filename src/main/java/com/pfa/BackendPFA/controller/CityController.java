package com.pfa.BackendPFA.controller;


import com.pfa.BackendPFA.entity.City;
import com.pfa.BackendPFA.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cities")
@CrossOrigin("*")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @GetMapping("/{cityName}")
    public ResponseEntity<City> getCityByName(@PathVariable String cityName) {
        City city = cityRepository.findByName(cityName);
        if (city != null) {
            return ResponseEntity.ok(city);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
