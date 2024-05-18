package com.pfa.BackendPFA.controller;


import com.pfa.BackendPFA.entity.Comment;
import com.pfa.BackendPFA.entity.CommentTmp;
import com.pfa.BackendPFA.model.CommentRequest;
import com.pfa.BackendPFA.repository.CommentRepository;

import com.pfa.BackendPFA.repository.PostRepository;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.CompositeName;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
@CrossOrigin("*")
public class CommentController {

    private final CommentRepository commentRepository;
    private final TouristRepository touristRepository;
    private final PostRepository postRepository;


    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable("id") int commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.map(comment -> new ResponseEntity<>(comment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> postComment(@RequestBody CommentRequest req) {
        try{
            Comment comment = new Comment();
            comment.setContent(req.getContent());
            comment.setTourist(touristRepository.findByEmail(req.getTouristEmail()).get());
            comment.setPost(postRepository.findById(req.getPostId()).get());
            commentRepository.save(comment);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
