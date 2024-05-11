package com.pfa.BackendPFA.repository;

import com.pfa.BackendPFA.entity.Comment;
import com.pfa.BackendPFA.entity.CommentTmp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
