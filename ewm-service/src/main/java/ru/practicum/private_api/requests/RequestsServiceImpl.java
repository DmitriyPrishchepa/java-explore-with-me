package ru.practicum.private_api.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.admin_api.users.UserRepository;
import ru.practicum.dtos.compilations.CompilationDto;
import ru.practicum.dtos.events.EventRequestStatusUpdateRequest;
import ru.practicum.dtos.events.EventRequestStatusUpdateResult;
import ru.practicum.dtos.events.State;
import ru.practicum.dtos.requests.ParticipationRequestDto;
import ru.practicum.dtos.requests.RequestStatus;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsRepository;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.requests.model.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestsServiceImpl implements RequestsService {

    private final RequestsRepository repository;
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;

    @Override
    public List<Request> getRequestsOfEvent(long userId, long eventId) {
        return repository.findAllByEvent(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request
    ) {
        isUserExists(userId);
        isEventExists(eventId);
        Event event = eventsRepository.getReferenceById(eventId);

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            result.setConfirmedRequests(new ArrayList<>());
            result.setRejectedRequests(new ArrayList<>());
            return result;
        } else {
            //Поиск всех запросов на событие
            List<Request> allRequests = repository.findAllByEvent(eventId);

            //преобразовываем в нужный формат
            List<ParticipationRequestDto> allRequestsDto = allRequests.stream()
                    .map(r -> new ParticipationRequestDto(
                                    r.getId(),
                                    r.getCreated(),
                                    r.getEvent(),
                                    r.getRequester(),
                                    r.getStatus().name()
                            )
                    ).toList();

            //фильтруем все запросы по статусу CONFIRMED и возвращаем их количество
            long confirmedCount = allRequestsDto.stream()
                    .filter(r -> "CONFIRMED".equals(r.getStatus()))
                    .count();

            List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
            List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ApiError(
                        HttpStatus.CONFLICT,
                        "For the requested operation the conditions are not met.",
                        "The participant limit has been reached"
                );
            }

            if (confirmedCount + 1 > event.getParticipantLimit()) {
                rejectedRequests.addAll(allRequestsDto.stream()
                        .filter(r -> !"CONFIRMED".equals(r.getStatus()))
                        .toList());
            } else {
                List<Request> requestsToProcess = repository.findAllById(
                        request.getRequestIds().stream().map(Integer::longValue).toList());

                for (Request requestToProcess : requestsToProcess) {
                    requestToProcess.setStatus(RequestStatus.CONFIRMED); // Обновляем статус
                    repository.save(requestToProcess); // Сохраняем изменения в базе данных

                    if ("CONFIRMED".equals(requestToProcess.getStatus().name())) {
                        confirmedRequests.add(new ParticipationRequestDto(
                                requestToProcess.getId(),
                                requestToProcess.getCreated(),
                                requestToProcess.getEvent(),
                                requestToProcess.getRequester(),
                                requestToProcess.getStatus().name()
                        ));
                    } else {
                        rejectedRequests.add(new ParticipationRequestDto(
                                requestToProcess.getId(),
                                requestToProcess.getCreated(),
                                requestToProcess.getEvent(),
                                requestToProcess.getRequester(),
                                requestToProcess.getStatus().name()
                        ));
                    }
                }
            }

            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }
    }

    @Override
    public Request cancelRequest(long userId, long requestId) {
        Request request = repository.findByIdAndRequester(requestId, userId);

        if (request == null) {
            throw new ApiError(
                    HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "Request with id=" + requestId + " was not found"
            );
        } else {
            request.setStatus(RequestStatus.PENDING);
            return repository.save(request);
        }
    }

    @Override
    public Request addRequestToEvent(long userId, long eventId) {
        isUserExists(userId);
        isEventExists(eventId);
        Event event = eventsRepository.getReferenceById(eventId);

        if (userId == event.getInitiator().getId()) {
            throw new ApiError(
                    HttpStatus.CONFLICT,
                    "Инициатор события не может участвовать в своём событии.",
                    "user is the initiator of the event and cannot create a request for participation."
            );
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ApiError(
                    HttpStatus.CONFLICT,
                    "Нельзя участвовать в неопубличном событии.",
                    "the event has not been published yet."
            );
        }

        long currentRequestsCount = repository.countByEvent(eventId);
        if (currentRequestsCount >= event.getParticipantLimit()) {
            throw new ApiError(
                    HttpStatus.CONFLICT,
                    "Лимит запросов на участие достигнут.",
                    "participant limit for the event has been reached."
            );
        }

        Request existingRequest = repository.findByRequesterAndEvent(userId, eventId);

        if (existingRequest != null) {
            throw new ApiError(
                    HttpStatus.CONFLICT,
                    "Integrity constraint has been violated.",
                    "could not execute statement; SQL [n/a]; constraint [uq_request]; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
            );
        }

        Request request = new Request();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        request.setCreated(formattedDate);
        request.setEvent(event.getId());
        request.setRequester(userId);

        if (event.isRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return repository.save(request);
    }

    @Override
    public List<Request> getRequestsByUser(long userId) {
        return repository.findByRequesterId(userId);
    }

    @Override
    public CompilationDto getCompilations(boolean pinned, int from, int size) {
        return null;
    }

    public void isEventExists(long id) {
        if (!eventsRepository.existsById(id)) {
            throw new ApiError(HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "Event with id=" + id + " was not found");
        }
    }

    public void isUserExists(long id) {
        if (!userRepository.existsById(id)) {
            throw new ApiError(HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "User with id=" + id + " was not found");
        }
    }
}
