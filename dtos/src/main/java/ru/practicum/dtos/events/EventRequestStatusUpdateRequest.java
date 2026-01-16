package ru.practicum.dtos.events;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;
    private String status;
}
