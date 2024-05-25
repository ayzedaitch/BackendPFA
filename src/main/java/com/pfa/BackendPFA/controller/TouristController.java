package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.entity.Tourist;
import com.pfa.BackendPFA.keycloak.KeycloakService;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tourists")
@CrossOrigin("*")
public class TouristController {

    private final TouristRepository touristRepository;
    private final KeycloakService keycloakService;

    @GetMapping
    public ResponseEntity<List<Tourist>> getAllTourists() {
        try{
            List<Tourist> tourists = touristRepository.findByIsEnabledTrue();
            if (!tourists.isEmpty()) {
                return ResponseEntity.ok(tourists);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/disabled")
    public ResponseEntity<List<Tourist>> getDisabledTourists() {
        try{
            List<Tourist> tourists = touristRepository.findByIsEnabledFalse();
            if (!tourists.isEmpty()) {
                return ResponseEntity.ok(tourists);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
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

    @PostMapping("/{email:.+}")
    public ResponseEntity<String> updateTouristInfo(@PathVariable("email") String email, @RequestBody Tourist tourist){
        Optional<Tourist> touristOptional = touristRepository.findByEmail(email);
        try {
            if (touristOptional.isPresent()){
                Tourist dbTourist = touristOptional.get();
                dbTourist.setFirstName(tourist.getFirstName());
                dbTourist.setLastName(tourist.getLastName());
                dbTourist.setPhoneNumber(tourist.getPhoneNumber());
                ResponseEntity<String> res= keycloakService.updateInfo(email, tourist);
                if(res.getStatusCode().isSameCodeAs(HttpStatus.OK)){
                    touristRepository.save(dbTourist);
                    return ResponseEntity.status(HttpStatus.OK).body("Tourist Info Updated");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res.getBody());
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tourist Does Not Exist");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred, Please Try Again");
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

        Optional<Tourist> touristOpt = touristRepository.findByEmail(currentEmail);
        try {
            if (touristOpt.isPresent()){
                if (keycloakService.isPasswordCorrect(currentEmail, password)){
                    Tourist tourist = touristOpt.get();
                    tourist.setEmail(newEmail);
                    ResponseEntity<String> res= keycloakService.updateEmail(currentEmail, newEmail);
                    if(res.getStatusCode().isSameCodeAs(HttpStatus.OK)){
                        touristRepository.save(tourist);
                        return ResponseEntity.status(HttpStatus.OK).body("Tourist Email Updated");
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res.getBody());
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Incorrect Password Or Account Not Verified Or Disabled");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tourist Does Not Exist");
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred, Please Try Again");
        }


    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updateTouristPassword(@RequestParam("email") String email, @RequestParam("password") String password){
        try {
            if (keycloakService.isPasswordCorrect(email, password)) {
                keycloakService.updatePassword(email);
                return ResponseEntity.status(HttpStatus.OK).body("Tourist Required Actions Updated");
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Incorrect Password Or Account Not Verified Or Disabled");
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred, Please Try Again");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteTourist(@RequestParam("email") String email,
                                                @RequestParam("password") String password){
        Optional<Tourist> touristOptional = touristRepository.findByEmail(email);
        try {
            if (touristOptional.isPresent()) {
                if (keycloakService.isPasswordCorrect(email, password)){
                    Tourist tourist = touristOptional.get();
                    tourist.setIsEnabled(false);
                    ResponseEntity<String> res = keycloakService.Disable(email);
                    if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)){
                        touristRepository.save(tourist);
                        return ResponseEntity.status(HttpStatus.OK).body("Tourist Disabled");
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res.getBody());
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Incorrect Password Or Account Not Verified Or Disabled");
                }

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Tourist Does Not Exist");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred, Please Try Again");
        }

    }



    @PostMapping("/blacklist")
    public ResponseEntity<String> blacklistTourist(@RequestParam("id") int id){
        Optional<Tourist> touristOptional = touristRepository.findById(id);
        try {
            if (touristOptional.isPresent()) {
                Tourist tourist = touristOptional.get();
                tourist.setIsEnabled(false);
                ResponseEntity<String> res = keycloakService.Disable(tourist.getEmail());
                if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)){
                    touristRepository.save(tourist);
                    return ResponseEntity.status(HttpStatus.OK).body("Tourist Blacklisted");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res.getBody());
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Tourist Does Not Exist");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred, Please Try Again");
        }

    }

    @PostMapping("/enable")
    public ResponseEntity<String> enableTourist(@RequestParam("id") int id){
        Optional<Tourist> touristOptional = touristRepository.findById(id);
        try {
            if (touristOptional.isPresent()) {
                Tourist tourist = touristOptional.get();
                tourist.setIsEnabled(true);
                ResponseEntity<String> res = keycloakService.Enable(tourist.getEmail());
                if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)){
                    touristRepository.save(tourist);
                    return ResponseEntity.status(HttpStatus.OK).body("Tourist Enabled");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res.getBody());
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Tourist Does Not Exist");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred, Please Try Again");
        }

    }

}
