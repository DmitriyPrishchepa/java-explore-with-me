package ru.practicum.dtos.events;

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
    private int from;
    private int size;
    private String remoteAddr;
    private String requestUri;

    public static SearchEventsDtoFiltered of(
            String text,
            List<Integer> categories,
            boolean paid,
            String rangeStart,
            String rangeEnd,
            boolean onlyAvailable,
            String sort,
            int from,
            int size,
            String remoteAddr,
            String requestUri
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
        searchEventsDtoFiltered.setRemoteAddr(remoteAddr);
        searchEventsDtoFiltered.setRequestUri(requestUri);
        return searchEventsDtoFiltered;
    }
}
