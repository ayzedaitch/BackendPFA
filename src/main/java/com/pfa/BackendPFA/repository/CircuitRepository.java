package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.Circuit;
import com.pfa.BackendPFA.entity.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircuitRepository extends JpaRepository<Circuit, Integer> {
    List<Circuit> findByTourist(Tourist tourist);
}
