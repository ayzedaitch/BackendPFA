package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.City;
import com.pfa.BackendPFA.entity.Monument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonumentRepository extends JpaRepository<Monument, Integer> {
    Monument findByName(String name);
    List<Monument> findByCity(City city);
}
