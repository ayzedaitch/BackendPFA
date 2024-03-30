package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.entity.Tourist;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tourists")
public class TouristController {

    private final TouristRepository touristRepository;
    private final PasswordEncoder passwordEncoder;


    @GetMapping
    public ResponseEntity<List<Tourist>> getAllTourists() {
        List<Tourist> tourists = touristRepository.findByIsEnabledTrue();
        if (!tourists.isEmpty()) {
            return ResponseEntity.ok(tourists);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{email:.+}")
    public ResponseEntity<Tourist> getTouristByEmail(@PathVariable("email") String email) {
        Optional<Tourist> touristOptional = touristRepository.findByEmail(email);
        if (touristOptional.isPresent()) {
            Tourist tourist = touristOptional.get();
            if (tourist.getIsEnabled()) {
                return ResponseEntity.ok(tourist);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update-email")
    public ResponseEntity<String> updateTouristEmail(@RequestParam("currentEmail") String currentEmail,
                                                     @RequestParam("newEmail") String newEmail,
                                                     @RequestParam("password") String password) {

        // Check if the new email already exists in the database
        Optional<Tourist> existingTouristOptional = touristRepository.findByEmail(newEmail);
        if (existingTouristOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("New email already exists");
        }

        //updating the email if the new email is not already taken
        Optional<Tourist> touristOptional = touristRepository.findByEmail(currentEmail);
        if (touristOptional.isPresent()) {
            Tourist tourist = touristOptional.get();
            if (passwordEncoder.matches(password, tourist.getPassword())) {
                tourist.setEmail(newEmail);
                touristRepository.save(tourist);
                return ResponseEntity.ok("Tourist email updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tourist not found with email: " + currentEmail);
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updateTouristPassword(@RequestParam("email") String email,
                                                        @RequestParam("currentPassword") String currentPassword,
                                                        @RequestParam("newPassword") String newPassword) {
        Optional<Tourist> touristOptional = touristRepository.findByEmail(email);
        if (touristOptional.isPresent()) {
            Tourist tourist = touristOptional.get();
            if (passwordEncoder.matches(currentPassword, tourist.getPassword())) {
                // Encode and update the new password
                String encodedNewPassword = passwordEncoder.encode(newPassword);
                tourist.setPassword(encodedNewPassword);
                touristRepository.save(tourist);
                return ResponseEntity.ok("Tourist password updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid current password");
            }
        } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tourist not found with email: " + email);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteTourist(@RequestParam("email") String email,
                                                @RequestParam("password") String password){
        Optional<Tourist> touristOptional = touristRepository.findByEmail(email);
        if (touristOptional.isPresent()) {
            Tourist tourist = touristOptional.get();
            tourist.setIsEnabled(false);
            touristRepository.save(tourist);
            return ResponseEntity.ok("Tourist disabled successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tourist not found with email: " + email);
        }
    }







}
