//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import ru.practicum.StatsRepository;
//import ru.practicum.StatsServiceImpl;
//import ru.practicum.dtos.events.EndpointHit;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//public class StatsServiceImplTest {
//
//    @Mock
//    StatsRepository endpointHitRepo;
//
//    @InjectMocks
//    StatsServiceImpl statsService;
//
//    EndpointHit mockedHit;
//
//    @BeforeEach
//    void setUp() {
//        mockedHit = Mockito.mock(EndpointHit.class);
//        when(mockedHit.getId()).thenReturn(1L);
//        when(mockedHit.getApp()).thenReturn("ewm-main-service");
//        when(mockedHit.getIp()).thenReturn("192.163.0.1");
//        when(mockedHit.getUri()).thenReturn("/events/1");
//        when(mockedHit.getTimestamp()).thenReturn("2022-09-06 11:00:23");
//    }
//
//    @Test
//    void addEndPointHitTest() {
//        EndpointHit endpointHit = new EndpointHit();
//        endpointHit.setId(1);
//        endpointHit.setApp("ewm-main-service");
//        endpointHit.setUri("/events/1");
//        endpointHit.setIp("192.163.0.1");
//        endpointHit.setTimestamp("2022-09-06 11:00:23");
//
//        statsService.addEndpointHit(endpointHit);
//
//        verify(endpointHitRepo, times(1)).save(Mockito.any(EndpointHit.class));
//    }
//
////    @Test
////    void getStatsViewTest() {
////        List<ViewStats> viewStatsList = new ArrayList<>();
////        ViewStats viewStats = new ViewStats();
////        viewStats.setApp("ewm-main-service");
////        viewStats.setUri("/events/1");
////        viewStats.setHits(6);
////
////        viewStatsList.add(viewStats);
////
////        Sort sort = Sort.by(Sort.Direction.ASC, "start")
////                .and(Sort.by(Sort.Direction.ASC, "end"));
////
////        PageRequest pageRequest = PageRequest.of(
////                0,
////                Integer.MAX_VALUE,
////                sort
////        );
////
////        when(viewStatsRepo.findAll(Mockito.any(PageRequest.class))).thenReturn(new PageImpl<>(viewStatsList));
////
////        List<ViewStats> result = statsService.getViewStats("start", "end", null, false);
////        assertThat(result, notNullValue());
////        assertThat(result.size(), equalTo(1));
////        assertThat(result.getFirst().getUri(), equalTo("/events/1"));
////    }
//}
