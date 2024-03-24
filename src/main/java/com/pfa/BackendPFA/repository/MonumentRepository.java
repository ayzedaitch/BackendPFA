package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.Monument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonumentRepository extends JpaRepository<Monument, Integer> {
}
