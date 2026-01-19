package ru.practicum.dtos.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchEventsDto {
    private List<Integer> users;
    private List<String> states;
    private List<Integer> categories;
    private String rangeStart;
    private String rangeEnd;
    @JsonProperty(defaultValue = "0")
    private int from;
    @JsonProperty(defaultValue = "10")
    private int size;

    public static SearchEventsDto of(
            List<Integer> users,
            List<String> states,
            List<Integer> categories,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    ) {
        SearchEventsDto searchEventsDto = new SearchEventsDto();
        searchEventsDto.setUsers(users);
        searchEventsDto.setStates(states);
        searchEventsDto.setCategories(categories);
        searchEventsDto.setRangeStart(rangeStart);
        searchEventsDto.setRangeEnd(rangeEnd);
        searchEventsDto.setFrom(from);
        searchEventsDto.setSize(size);
        return searchEventsDto;
    }
}
