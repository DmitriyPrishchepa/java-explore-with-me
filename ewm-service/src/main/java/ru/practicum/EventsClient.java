package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStatsResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class EventsClient {
    protected final RestTemplate rest;

    @Value("${stats.server.url}")
    private String BASE_URL;

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

        String uriString = uriComponentsBuilder.build(false).toUriString();

        try {
            ResponseEntity<List<ViewStatsResponse>> response = rest.exchange(
                    uriString,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ViewStatsResponse>>() {
                    }
            );
            List<ViewStatsResponse> result = response.getBody();
            return result;
        } catch (Exception e) {
            System.out.print("Error occurred while making request: ");
            e.printStackTrace();
            throw e;
        }
    }
}