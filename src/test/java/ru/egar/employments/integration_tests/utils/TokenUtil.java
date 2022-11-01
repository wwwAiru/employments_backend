package ru.egar.employments.integration_tests.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.egar.employments.integration_tests.dto.KeycloakTokenDto;

import java.util.Collections;
import java.util.Objects;

@Component
public class TokenUtil {

    private static final String AUTH_SERVER_URL = System.getenv("KEYCLOAK_AUTH_URL");

    private static final String CLIENT_ID = System.getenv("KEYCLOAK_CLIENT_ID");

    private static final String CLIENT_SECRET = System.getenv("KEYCLOAK_CLIENT_SECRET");

    private static final String EGAR_ID = System.getenv("EGAR_ID");

    private static final String PASSWORD = System.getenv("EGAR_PASSWORD");

    public static String getTokenFromKeycloak() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("password"));
        params.put("client_id", Collections.singletonList(CLIENT_ID));
        params.put("client_secret", Collections.singletonList(CLIENT_SECRET));
        params.put("username", Collections.singletonList(EGAR_ID));
        params.put("password", Collections.singletonList(PASSWORD));
        KeycloakTokenDto keycloakTokenDto = restTemplate.postForObject(
                AUTH_SERVER_URL, new HttpEntity<>(params, httpHeaders), KeycloakTokenDto.class);
        return Objects.requireNonNull(keycloakTokenDto).getAccessToken();
    }
}
