package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.entity.Comment;
import com.pfa.BackendPFA.entity.CommentTmp;
import com.pfa.BackendPFA.repository.CommentRepository;
import com.pfa.BackendPFA.repository.CommentTmpRepository;
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

    @Autowired
    private CommentTmpRepository commentTmpRepository;
    private CommentRepository commentRepository;

    @GetMapping
    public ResponseEntity<List<CommentTmp>> getAllComments() {
        List<CommentTmp> comments = commentTmpRepository.findAll();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /*
    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable("id") int commentId) {
        // Check if the comment exists
        if (commentTmpRepository.existsById(commentId)) {
            commentTmpRepository.deleteById(commentId);
            return "Comment with ID " + commentId + " deleted successfully.";
        } else {
            return "Comment with ID " + commentId + " not found.";
        }
    }
    */

    @DeleteMapping("/{id}")
    public String moveCommentToActualTableAndDelete(@PathVariable("id") int commentId) {
        // Check if the comment exists in the temporary table
        CommentTmp commentTmp = commentTmpRepository.findById(commentId).orElse(null);
        if (commentTmp != null) {
            // Save the comment to the actual table
            Comment comment = new Comment();
            comment.setContent(commentTmp.getContent());
            comment.setCreatedAt(commentTmp.getCreatedAt());
            comment.setPost(commentTmp.getPost());
            comment.setTourist(comment.getTourist());

            commentRepository.save(comment);

            // Delete the comment from the temporary table
            commentTmpRepository.deleteById(commentId);
            return "Comment moved to actual table and deleted from temporary table successfully.";
        } else {
            return "Comment with ID " + commentId + " not found in temporary table.";
        }
    }

    @PostMapping
    public ResponseEntity<CommentTmp> postComment(@RequestBody CommentTmp comment) {
        CommentTmp savedComment = commentTmpRepository.save(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }


}
