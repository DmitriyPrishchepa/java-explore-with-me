package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStatsResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventsClient {
    protected final RestTemplate rest;
    private static final String BASE_URL = "http://stats-server:9090";

    public EventsClient(RestTemplate restTemplate) {
        this.rest = restTemplate;
    }

    public void addHits(EndpointHit endpointHitDto) {
        rest.postForEntity(BASE_URL + "/hit", endpointHitDto, Void.class);
    }

    public List<ViewStatsResponse> getStats(String start, String end, List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Даты начала и окончания должны быть заданы");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Укажите правильный формат
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/stats")
                .queryParam("start", startDate.format(formatter)) // Преобразуем обратно в строку для запроса
                .queryParam("end", endDate.format(formatter));

        if (uris != null && !uris.isEmpty()) {
            uriComponentsBuilder.queryParam("uris", uris);
        }

        if (unique != null) {
            uriComponentsBuilder.queryParam("unique", unique);
        }

        ParameterizedTypeReference<List<ViewStatsResponse>> typeRef = new ParameterizedTypeReference<List<ViewStatsResponse>>() {
        };
        return rest.exchange(uriComponentsBuilder.encode().toUriString(),
                HttpMethod.GET, null, typeRef).getBody();
    }
}

//    private final RestClient restClient;
//    private static final String BASE_URL = "http://stats-server:9090";
//
//    public EventsClient(RestClient restClient) {
//        this.restClient = restClient;
//    }
//
//    public void addHits(EndpointHit endpointHitDto) {
//        restClient.post().uri(BASE_URL + "/hit")
//                .body(endpointHitDto)
//                .retrieve()
//                .toBodilessEntity();
//    }
//
//    public List<ViewStatsResponse> getStats(String start, String end, List<String> uris, Boolean unique) {
//        if (start == null || end == null) {
//            throw new IllegalArgumentException("Даты начала и окончания должны быть заданы");
//        }
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Укажите правильный формат
//        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
//        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
//
//        if (endDate.isBefore(startDate)) {
//            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
//        }
//
//        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
//                .fromHttpUrl(BASE_URL + "/stats")
//                .queryParam("start", startDate.format(formatter)) // Преобразуем обратно в строку для запроса
//                .queryParam("end", endDate.format(formatter));
//
//
//        if (uris != null && !uris.isEmpty()) {
//            uriComponentsBuilder.queryParam("uris", uris);
//        }
//
//        if (unique != null) {
//            uriComponentsBuilder.queryParam("unique", unique);
//        }
//
//        return restClient.get()
//                .uri(uriComponentsBuilder.encode().toUriString())
//                .retrieve()
//                .body(new ParameterizedTypeReference<List<ViewStatsResponse>>() {
//                });
//    }


