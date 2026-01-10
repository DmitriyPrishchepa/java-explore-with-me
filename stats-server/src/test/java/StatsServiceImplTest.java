import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.StatsRepository;
import ru.practicum.StatsServiceImpl;
import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStatsResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StatsServiceImplTest {

    @Mock
    StatsRepository endpointHitRepo;

    @InjectMocks
    StatsServiceImpl statsService;

    EndpointHit mockedHit;

    @BeforeEach
    void setUp() {
        mockedHit = Mockito.mock(EndpointHit.class);
        when(mockedHit.getId()).thenReturn(1L);
        when(mockedHit.getApp()).thenReturn("ewm-main-service");
        when(mockedHit.getIp()).thenReturn("192.163.0.1");
        when(mockedHit.getUri()).thenReturn("/events/1");
        when(mockedHit.getTimestamp()).thenReturn("2022-09-06 11:00:23");
    }

    @Test
    void addEndPointHitTest() {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setId(1);
        endpointHit.setApp("ewm-main-service");
        endpointHit.setUri("/events/1");
        endpointHit.setIp("192.163.0.1");
        endpointHit.setTimestamp("2022-09-06 11:00:23");

        statsService.addEndpointHit(endpointHit);

        verify(endpointHitRepo, times(1)).save(Mockito.any(EndpointHit.class));
    }

    @Test
    void getStatsViewTest() {
        List<EndpointHit> endpointHits = new ArrayList<>();
        endpointHits.add(mockedHit);

        when(endpointHitRepo.findWithSort(anyString(), anyString())).thenReturn(endpointHits);

        List<ViewStatsResponse> viewStats = statsService.getViewStats("2022-09-05 00:00:00", "2022-09-07 23:59:59", null, false);

        assertEquals(1, viewStats.size());

        ViewStatsResponse statsResponse = viewStats.getFirst();
        assertEquals("ewm-main-service", statsResponse.getApp());
        assertEquals("/events/1", statsResponse.getUri());
        assertEquals(1L, statsResponse.getHits());
    }
}
