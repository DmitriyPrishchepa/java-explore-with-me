package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStatsResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventsClient extends BaseClient {

    @Autowired
    public EventsClient(RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory("http://stats-server:9090"))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build());
    }

    public void addHits(EndpointHit endpointHit) {
        post(endpointHit);
    }

    public ResponseEntity<List<ViewStatsResponse>> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        if (uris != null) {
            parameters.put("uris", uris);
        }
        parameters.put("unique", unique);

        return get("/stats", null, parameters);
    }
}
