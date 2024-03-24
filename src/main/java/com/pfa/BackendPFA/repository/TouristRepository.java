package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TouristRepository extends JpaRepository<Tourist, Integer> {
}
