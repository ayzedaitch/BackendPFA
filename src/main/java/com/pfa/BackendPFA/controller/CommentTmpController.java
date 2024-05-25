package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.entity.Comment;
import com.pfa.BackendPFA.entity.CommentTmp;
import com.pfa.BackendPFA.model.CommentRequest;
import com.pfa.BackendPFA.repository.CommentRepository;
import com.pfa.BackendPFA.repository.CommentTmpRepository;
import com.pfa.BackendPFA.repository.PostRepository;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commentsTmp")
@CrossOrigin("*")
public class CommentTmpController {

    private final CommentTmpRepository commentTmpRepository;
    private final CommentRepository commentRepository;
    private final TouristRepository touristRepository;
    private final PostRepository postRepository;

    @GetMapping
    public ResponseEntity<?> getAllComments() {
        try{
            List<CommentTmp> comments = commentTmpRepository.findAll();
            return ResponseEntity.ok(comments);
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/validate/{id}")
    public ResponseEntity<?> moveCommentToActualTableAndDelete(@PathVariable("id") int commentId) {
        try{
            // Check if the comment exists in the temporary table
            CommentTmp commentTmp = commentTmpRepository.findById(commentId).orElse(null);
            if (commentTmp != null) {
                // Save the comment to the actual table
                System.out.println(1);
                Comment comment = new Comment();
                comment.setContent(commentTmp.getContent());
                comment.setCreatedAt(commentTmp.getCreatedAt());
                comment.setPost(commentTmp.getPost());
                comment.setTourist(commentTmp.getTourist());

                commentRepository.save(comment);

                // Delete the comment from the temporary table
                commentTmpRepository.deleteById(commentId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable("id") int commentId) {
        try{
            // Check if the comment exists in the temporary table
            CommentTmp commentTmp = commentTmpRepository.findById(commentId).orElse(null);
            if (commentTmp != null) {
                commentTmpRepository.deleteById(commentId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }


    @PostMapping
    public ResponseEntity<?> postComment(@RequestBody CommentRequest req) {
        try{
            CommentTmp comment = new CommentTmp();
            comment.setContent(req.getContent());
            comment.setTourist(touristRepository.findByEmail(req.getTouristEmail()).get());
            comment.setPost(postRepository.findById(req.getPostId()).get());
            commentTmpRepository.save(comment);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }


}
