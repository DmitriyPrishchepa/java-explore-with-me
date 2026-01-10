package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStats;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepositoryEndpointHit statsRepositoryEndpointHit;
    private final StatsRepositoryViewStats statsRepositoryViewStats;

    @Override
    public void addEndpointHit(EndpointHit endpointHit) {
        statsRepositoryEndpointHit.save(endpointHit);
    }

    @Override
    public List<ViewStats> getViewStats(String start, String end, List<String> uris, boolean unique) {
        Sort sort = Sort.by(Sort.Direction.ASC, "start")
                .and(Sort.by(Sort.Direction.ASC, "end"));

        if (uris != null && !uris.isEmpty()) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "uri"));
        }

        PageRequest pageRequest = PageRequest.of(
                0,
                Integer.MAX_VALUE,
                sort
        );

        return statsRepositoryViewStats.findAll(pageRequest).getContent();
    }
}
