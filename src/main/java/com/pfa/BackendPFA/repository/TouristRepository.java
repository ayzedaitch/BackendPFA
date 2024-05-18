package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.Tourist;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TouristRepository extends JpaRepository<Tourist, Integer> {

    Optional<Tourist> findByEmail(String email);
    List<Tourist> findByIsEnabledTrue();
    List<Tourist> findByIsEnabledFalse();

}
