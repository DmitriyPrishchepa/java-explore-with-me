//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import ru.practicum.StatsController;
//import ru.practicum.StatsService;
//import ru.practicum.dtos.events.EndpointHit;
//import ru.practicum.dtos.events.ViewStats;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//public class StatsControllerTest {
//    @Mock
//    StatsService statsService;
//
//    @InjectMocks
//    StatsController statsController;
//
//    private final ObjectMapper mapper = new ObjectMapper();
//    MockMvc mvc;
//
//    @BeforeEach
//    void setUp() {
//        mvc = MockMvcBuilders
//                .standaloneSetup(statsController)
//                .build();
//    }
//
//    @Test
//    void addEndPointHitTest() throws Exception {
//        EndpointHit endpointHit = new EndpointHit();
//        endpointHit.setId(1);
//        endpointHit.setApp("ewm-main-service");
//        endpointHit.setUri("/events/1");
//        endpointHit.setIp("192.163.0.1");
//        endpointHit.setTimestamp("2022-09-06 11:00:23");
//
//        mvc.perform(post("/hit")
//                        .content(mapper.writeValueAsString(endpointHit))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(statsService, times(1)).addEndpointHit(Mockito.any(EndpointHit.class));
//    }
//
//    @Test
//    void getStatsViewTest() throws Exception {
//        // Создаём список ViewStats для имитации результатов
//        List<ViewStats> viewStatsList = new ArrayList<>();
//        ViewStats viewStats = new ViewStats(
//                "ewm-main-service",
//                "/events/1",
//                6
//        );
//        viewStatsList.add(viewStats);
//
//        Sort sort = Sort.by(Sort.Direction.ASC, "start")
//                .and(Sort.by(Sort.Direction.ASC, "end"));
//
//        PageRequest pageRequest = PageRequest.of(
//                0,
//                Integer.MAX_VALUE,
//                sort
//        );
//
//        when(statsService.getViewStats(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
//                .thenReturn(viewStatsList);
//
//        mvc.perform(get("/stats")
//                        .param("start", "2022-01-01")
//                        .param("end", "2023-01-01"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("[0].app", is("ewm-main-service")))
//                .andExpect(jsonPath("[0].uri", is("/events/1")))
//                .andExpect(jsonPath("[0].hits", is(6)));
//    }
//}
