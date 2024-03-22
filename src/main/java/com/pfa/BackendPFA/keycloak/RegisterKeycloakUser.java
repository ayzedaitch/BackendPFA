package com.pfa.BackendPFA.keycloak;

import com.pfa.BackendPFA.model.ModelTest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RegisterKeycloakUser {
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

    public String Register(ModelTest modelTest){
        try {
            UserRepresentation user = new UserRepresentation();
            user.setEmail(modelTest.getEmail());
            user.setEnabled(true);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(modelTest.getPassword());

            user.setCredentials(List.of(passwordCred));
            keycloak.realm(keycloakAdminRealm).users().create(user);

            String userId = keycloak.realm(keycloakAdminRealm).users().search(user.getUsername()).get(0).getId();
            String roleName = "tourist";
            RoleRepresentation role = keycloak.realm(keycloakAdminRealm).roles().get(roleName).toRepresentation();
            keycloak.realm(keycloakAdminRealm).users().get(userId).roles().realmLevel().add(List.of(role));

            return "success";
        } catch (Exception e){
            return e.toString();
        }
    }
}
