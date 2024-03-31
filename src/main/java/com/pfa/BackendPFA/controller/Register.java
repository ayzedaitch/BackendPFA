package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.model.TouristReq;
import com.pfa.BackendPFA.keycloak.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class Register {
    private final KeycloakService keycloakService;
    @PostMapping("/register")
    public ResponseEntity<String> RegisterTourist(@RequestBody TouristReq req){
        return keycloakService.Register(req);
    }
}
