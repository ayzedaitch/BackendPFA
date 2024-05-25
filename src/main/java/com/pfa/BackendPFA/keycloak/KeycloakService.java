package com.pfa.BackendPFA.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfa.BackendPFA.entity.Tourist;
import com.pfa.BackendPFA.model.TokenResponse;
import com.pfa.BackendPFA.model.TouristReq;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final TouristRepository touristRepository;
    private final String keycloakAdminUsername = "noreply.tourismapp@gmail.com";
    private final String keycloakAdminPassword = "admin";
    private final String keycloakAdminRealm = "ENSA";
    private final String keycloakAdminClientId = "admin-cli";
    private final String keycloakAdminGrantType = "password";
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final String keycloakServerUrl = "http://localhost:8080";
    private final Keycloak keycloak = KeycloakBuilder.builder()
            .serverUrl(keycloakServerUrl)
            .realm(keycloakAdminRealm)
            .username(keycloakAdminUsername)
            .password(keycloakAdminPassword)
            .clientId(keycloakAdminClientId)
            .grantType(keycloakAdminGrantType)
            .build();

    public ResponseEntity<String> Register(TouristReq touristReq){
        Tourist tourist = Tourist.builder()
                .firstName(touristReq.getFirstName())
                .lastName(touristReq.getLastName())
                .email(touristReq.getEmail())
                .phoneNumber(touristReq.getPhoneNumber())
                .isEnabled(true)
                .build();

        try {
            UsersResource userResource = keycloak.realm(keycloakAdminRealm).users();
            List<UserRepresentation> existingUsers = userResource.search(tourist.getEmail());

            if (!existingUsers.isEmpty()) {
                // User with the same email already exists
                throw new RuntimeException();
            }

            UserRepresentation user = new UserRepresentation();
            user.setEmail(tourist.getEmail());
            user.setFirstName(tourist.getFirstName());
            user.setLastName(tourist.getLastName());
            user.setEnabled(true);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(touristReq.getPassword());
            passwordCred.setTemporary(false);

            user.setCredentials(List.of(passwordCred));
            keycloak.realm(keycloakAdminRealm).users().create(user);

            String userId = keycloak.realm(keycloakAdminRealm).users().search(user.getEmail()).get(0).getId();
            String roleName = "tourist";
            RoleRepresentation role = keycloak.realm(keycloakAdminRealm).roles().get(roleName).toRepresentation();
            keycloak.realm(keycloakAdminRealm).users().get(userId).roles().realmLevel().add(List.of(role));

            touristRepository.save(tourist);

            return ResponseEntity.ok("Tourist Created");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<String> updateEmail(String currentEmail, String newEmail){
        try{
            UsersResource usersResource = keycloak.realm(keycloakAdminRealm).users();
            String userId = keycloak.realm(keycloakAdminRealm).users().search(currentEmail).get(0).getId();
            UserRepresentation user = usersResource.get(userId).toRepresentation();
            user.setEmail(newEmail);
            user.setEmailVerified(false);
            usersResource.get(userId).update(user);
            return ResponseEntity.status(HttpStatus.OK).body("Tourist Email Updated");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public void updatePassword(String email) {
        UsersResource usersResource = keycloak.realm(keycloakAdminRealm).users();
        String userId = keycloak.realm(keycloakAdminRealm).users().search(email).get(0).getId();
        UserRepresentation user = usersResource.get(userId).toRepresentation();
        user.setRequiredActions(Collections.singletonList("UPDATE_PASSWORD"));
        user.setEmailVerified(false);
        usersResource.get(userId).update(user);
    }

    public ResponseEntity<String> Disable(String email){
        try{
            UsersResource usersResource = keycloak.realm(keycloakAdminRealm).users();
            String userId = keycloak.realm(keycloakAdminRealm).users().search(email).get(0).getId();
            UserRepresentation user = usersResource.get(userId).toRepresentation();
            user.setEnabled(false);
            usersResource.get(userId).update(user);
            return ResponseEntity.status(HttpStatus.OK).body("Tourist Disabled");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<String> Enable(String email){
        try{
            UsersResource usersResource = keycloak.realm(keycloakAdminRealm).users();
            String userId = keycloak.realm(keycloakAdminRealm).users().search(email).get(0).getId();
            UserRepresentation user = usersResource.get(userId).toRepresentation();
            user.setEnabled(true);
            usersResource.get(userId).update(user);
            return ResponseEntity.status(HttpStatus.OK).body("Tourist Enabled");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public boolean isPasswordCorrect(String email, String password){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/realms/ENSA/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> formData = new HashMap<>();
        formData.put("client_id", "pfa");
        formData.put("client_secret", clientSecret);
        formData.put("grant_type", "password");
        formData.put("username", email);
        formData.put("password", password);

        StringBuilder requestBody = new StringBuilder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            // entry === key-value pair
            requestBody.append(entry.getKey());
            requestBody.append("=");
            requestBody.append(entry.getValue());
            requestBody.append("&");
        }
        // Remove the last "&" character
        requestBody.deleteCharAt(requestBody.length() - 1);

        ResponseEntity<String> response = null;
        try {
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            response = restTemplate.postForEntity(url, request, String.class);
            endUserSession(response);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e){
            endUserSession(response);
            return false;
        }
    }

    private void endUserSession(ResponseEntity<String> response) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TokenResponse tokenResponse = objectMapper.readValue(response.getBody(), TokenResponse.class);
            String url = "http://localhost:8080/realms/ENSA/protocol/openid-connect/logout";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBearerAuth(tokenResponse.getAccessToken());

            Map<String, String> formData = new HashMap<>();
            formData.put("client_id", "pfa");
            formData.put("client_secret", clientSecret);
            formData.put("refresh_token", tokenResponse.getRefreshToken());

            StringBuilder requestBody = new StringBuilder();
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                requestBody.append(entry.getKey());
                requestBody.append("=");
                requestBody.append(entry.getValue());
                requestBody.append("&");
            }
            // Remove the last "&" character
            requestBody.deleteCharAt(requestBody.length() - 1);

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> res = restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public ResponseEntity<String> updateInfo(String email, Tourist tourist) {
        try{
            UsersResource usersResource = keycloak.realm(keycloakAdminRealm).users();
            String userId = keycloak.realm(keycloakAdminRealm).users().search(email).get(0).getId();
            UserRepresentation user = usersResource.get(userId).toRepresentation();
            user.setFirstName(tourist.getFirstName());
            user.setLastName(tourist.getLastName());
            usersResource.get(userId).update(user);
            return ResponseEntity.status(HttpStatus.OK).body("Tourist Info Updated");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
