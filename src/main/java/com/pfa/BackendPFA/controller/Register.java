package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.keycloak.RegisterKeycloakUser;
import com.pfa.BackendPFA.model.ModelTest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class Register {
    private final RegisterKeycloakUser registerKeycloakUser;
    @PostMapping("/register")
    public ResponseEntity<String> Register(@RequestBody ModelTest req){
        return registerKeycloakUser.Register(req);
    }
}
