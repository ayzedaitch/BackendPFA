package com.pfa.BackendPFA.keycloak;

import com.pfa.BackendPFA.entity.Tourist;
import com.pfa.BackendPFA.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterKeycloakUser {
    private final TouristRepository touristRepository;
    private final PasswordEncoder passwordEncoder;
    private final String keycloakAdminUsername = "noreply.tourismapp@gmail.com";
    private final String keycloakAdminPassword = "admin";
    private final String keycloakAdminRealm = "ENSA";
    private final String keycloakAdminClientId = "admin-cli";
    private final String keycloakAdminGrantType = "password";

    private final String keycloakServerUrl = "http://localhost:8080";
    private final Keycloak keycloak = KeycloakBuilder.builder()
            .serverUrl(keycloakServerUrl)
            .realm(keycloakAdminRealm)
            .username(keycloakAdminUsername)
            .password(keycloakAdminPassword)
            .clientId(keycloakAdminClientId)
            .grantType(keycloakAdminGrantType)
            .build();

    public ResponseEntity<String> Register(Tourist tourist){
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
            passwordCred.setValue(tourist.getPassword());

            user.setCredentials(List.of(passwordCred));
            keycloak.realm(keycloakAdminRealm).users().create(user);

            String userId = keycloak.realm(keycloakAdminRealm).users().search(user.getUsername()).get(0).getId();
            String roleName = "tourist";
            RoleRepresentation role = keycloak.realm(keycloakAdminRealm).roles().get(roleName).toRepresentation();
            keycloak.realm(keycloakAdminRealm).users().get(userId).roles().realmLevel().add(List.of(role));

            tourist.setPassword(passwordEncoder.encode(tourist.getPassword()));
            touristRepository.save(tourist);

            return ResponseEntity.ok("Tourist Created");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An Error Occurred, Please Try Again");
        }
    }
}
