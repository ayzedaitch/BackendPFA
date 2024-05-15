package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.entity.*;
import com.pfa.BackendPFA.repository.CircuitRepository;
import com.pfa.BackendPFA.repository.CommentRepository;
import com.pfa.BackendPFA.repository.PostRepository;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CommentRepository commentRepository;

    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "") String city,
                                         @RequestParam(defaultValue = "") String possession,
                                         @RequestParam(defaultValue = "") String sort){

        try{
            Pageable pageable = PageRequest.of(page, 5);
            if (!(city == null || city.isEmpty())){
                List<Post> posts = postRepository.findByCircuitCityName(city,pageable).getContent();
                return ResponseEntity.ok(mapPosts(posts));
            }
            else if (!(possession == null || possession.isEmpty())){
                List<Post> posts = postRepository.findByTouristEmail(possession, pageable).getContent();
                return ResponseEntity.ok(mapPosts(posts));
            } else if (sort.equalsIgnoreCase("mostVoted")){
                List<Post> posts = postRepository.findAllByOrderByVotesDesc(pageable).getContent();
                return ResponseEntity.ok(mapPosts(posts));
            } else {
                List<Post> posts = postRepository.findAll(pageable).getContent();
                return ResponseEntity.ok(mapPosts(posts));
            }

        } catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    private List<?> mapPosts(List<Post> posts){
        List<Map<String, Object>> response = new ArrayList<>();
        for (Post post:
                posts) {
            List<Comment> comments = commentRepository.findByPost(post);
            Map<String, Object> calculationRequirements = new HashMap<>();
            calculationRequirements.put("departure", new double[]{
                    post.getCircuit().getDepartureMonument().getLatitude(), post.getCircuit().getDepartureMonument().getLongitude()
            });
            List<double[]> monumentsCoordinates = new ArrayList<>();

            for (Monument monument:
                    post.getCircuit().getMonuments()) {
                // return just the monuments without the departure
                if (monument.getId() != post.getCircuit().getDepartureMonument().getId()){
                    monumentsCoordinates.add(new double[]{monument.getLatitude(),monument.getLongitude()});
                }
            }
            calculationRequirements.put("coordinates", monumentsCoordinates);

            Map<String, Object> result = new HashMap<>();
            result.put("calculationRequirements", calculationRequirements);
            result.put("content", post.getContent());
            result.put("votes", post.getVotes());
            result.put("comments", comments);

            response.add(result);
        }
        return response;
    }

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
