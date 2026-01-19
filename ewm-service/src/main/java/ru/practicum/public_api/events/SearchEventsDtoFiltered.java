package ru.practicum.public_api.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchEventsDtoFiltered {
    private String text;
    private List<Integer> categories;
    private boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private boolean onlyAvailable;
    private String sort;
    @JsonProperty(defaultValue = "0")
    private int from;
    @JsonProperty(defaultValue = "10")
    private int size;

    public static SearchEventsDtoFiltered of(
            String text,
            List<Integer> categories,
            boolean paid,
            String rangeStart,
            String rangeEnd,
            boolean onlyAvailable,
            String sort,
            int from,
            int size
    ) {
        SearchEventsDtoFiltered searchEventsDtoFiltered = new SearchEventsDtoFiltered();
        searchEventsDtoFiltered.setText(text);
        searchEventsDtoFiltered.setCategories(categories);
        searchEventsDtoFiltered.setPaid(paid);
        searchEventsDtoFiltered.setRangeStart(rangeStart);
        searchEventsDtoFiltered.setRangeEnd(rangeEnd);
        searchEventsDtoFiltered.setOnlyAvailable(onlyAvailable);
        searchEventsDtoFiltered.setSort(sort);
        searchEventsDtoFiltered.setFrom(from);
        searchEventsDtoFiltered.setSize(size);
        return searchEventsDtoFiltered;
    }
}
