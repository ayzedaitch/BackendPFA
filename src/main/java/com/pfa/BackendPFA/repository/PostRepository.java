package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findByCircuitCityName(String name, Pageable pageable);
    Page<Post> findByTouristEmail(String email, Pageable pageable);
    Page<Post> findAllByOrderByVotesDesc(Pageable pageable);
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
