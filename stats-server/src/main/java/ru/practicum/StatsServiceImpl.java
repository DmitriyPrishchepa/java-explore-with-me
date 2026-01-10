package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStats;
import ru.practicum.dtos.events.ViewStatsResponse;
import ru.practicum.mapper.ViewStatsMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void addEndpointHit(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStatsResponse> getViewStats(String start, String end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startDate = formatter.format(LocalDateTime.parse(start, formatter));
        String endDate = formatter.format(LocalDateTime.parse(end, formatter));

        List<EndpointHit> endpointHits = statsRepository.findWithSort(startDate, endDate);

        if (uris != null && !uris.isEmpty()) {
            endpointHits = endpointHits.stream()
                    .filter(endpointHit -> uris.contains(endpointHit.getUri()))
                    .toList();
        }


        if (unique) {
            endpointHits = new ArrayList<>(new HashSet<>(endpointHits));
        }

        Map<String, ViewStats> viewStatsMap = new HashMap<>();

        for (EndpointHit hit : endpointHits) {
            String key = hit.getApp() + "-" + hit.getUri();
            if (!viewStatsMap.containsKey(key)) {
                viewStatsMap.put(key, new ViewStats(hit.getApp(), hit.getUri(), 0));
            }
            viewStatsMap.get(key).setHits(viewStatsMap.get(key).getHits() + 1);
        }

        return viewStatsMap.values().stream()
                .map(ViewStatsMapper::map)
                .collect(Collectors.toList());
    }
}
