package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
