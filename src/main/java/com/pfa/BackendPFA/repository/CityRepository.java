package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.City;
import com.pfa.BackendPFA.entity.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {

    City findByName(String name);
}
