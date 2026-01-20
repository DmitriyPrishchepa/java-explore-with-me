package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dtos.events.ViewStatsResponse;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<List<ViewStatsResponse>> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> void post(T body) {
        post("/hit", null, null, body);
    }

    protected <T> ResponseEntity<List<ViewStatsResponse>> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    private <T> ResponseEntity<List<ViewStatsResponse>> makeAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<List<ViewStatsResponse>> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(
                        path,
                        method,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        },
                        parameters);
            } else {
                statsServerResponse = rest.exchange(
                        path,
                        method,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        });
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
        return prepareStatsResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private ResponseEntity<List<ViewStatsResponse>> prepareStatsResponse(ResponseEntity<List<ViewStatsResponse>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}