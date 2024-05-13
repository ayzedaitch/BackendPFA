package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.entity.Circuit;
import com.pfa.BackendPFA.entity.Monument;
import com.pfa.BackendPFA.entity.Post;
import com.pfa.BackendPFA.entity.Tourist;
import com.pfa.BackendPFA.repository.CircuitRepository;
import com.pfa.BackendPFA.repository.PostRepository;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostRepository postRepository;
    private final CircuitRepository circuitRepository;
    private final TouristRepository touristRepository;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestParam("email") String email,
                                        @RequestParam("circuitId") int circuitId,
                                        @RequestParam("content") String content){
        try{
            Optional<Tourist> touristOpt = touristRepository.findByEmail(email);
            Optional<Circuit> circuitOpt = circuitRepository.findById(circuitId);

            if (touristOpt.isPresent() && circuitOpt.isPresent()){
                Circuit circuit = circuitOpt.get();
                Tourist tourist = touristOpt.get();

                if (!circuit.getTourist().equals(tourist)){
                    return ResponseEntity.notFound().build();
                }

                Post post = new Post();
                post.setCircuit(circuit);
                post.setTourist(tourist);
                post.setContent(content);

                postRepository.save(post);

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/fork")
    public ResponseEntity<?> forkCircuit(@RequestParam("email") String emailTarget,
                                         @RequestParam("circuitId") int circuitId){
        try{
            Optional<Tourist> touristOpt = touristRepository.findByEmail(emailTarget);
            Optional<Circuit> circuitOpt = circuitRepository.findById(circuitId);

            if (touristOpt.isPresent() && circuitOpt.isPresent()) {
                Circuit circuit = circuitOpt.get();
                Tourist tourist = touristOpt.get();

                List<Monument> monuments = new ArrayList<>(circuit.getMonuments());
                // to see why we created a new instance for monuments
                // check: https://chatgpt.com/c/b6219731-62fb-4117-b0ac-e9ab55f61c36

                Circuit forkedCircuit = new Circuit();
                forkedCircuit.setCity(circuit.getCity());
                forkedCircuit.setTourist(tourist);
                forkedCircuit.setMonuments(monuments);
                forkedCircuit.setDepartureMonument(circuit.getDepartureMonument());
                forkedCircuit.setDepartureTime(circuit.getDepartureTime());
                forkedCircuit.setCreatedAt(new Timestamp(System.currentTimeMillis()));

                circuitRepository.save(forkedCircuit);

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/votes")
    public ResponseEntity<?> vote(@RequestParam("postId") int postId, @RequestParam("votes") int votes){
        try{
            Optional<Post> postOpt = postRepository.findById(postId);

            if (postOpt.isPresent()){
                Post post = postOpt.get();
                post.setVotes(votes);

                postRepository.save(post);

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
