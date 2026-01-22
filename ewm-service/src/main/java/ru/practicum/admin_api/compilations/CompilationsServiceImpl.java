package ru.practicum.admin_api.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin_api.categories.mapper.CategoryMapper;
import ru.practicum.admin_api.compilations.entities.Compilation;
import ru.practicum.admin_api.compilations.entities.CompilationEvents;
import ru.practicum.admin_api.compilations.mapper.CompilationsEventsMapper;
import ru.practicum.admin_api.users.mapper.UserMapper;
import ru.practicum.dtos.compilations.CompilationDto;
import ru.practicum.dtos.compilations.NewCompilationDto;
import ru.practicum.dtos.compilations.UpdateCompilationRequest;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsRepository;
import ru.practicum.private_api.events.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {

    private final EventsRepository eventsRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationEventsRepository compilationEventsRepository;
    private final CompilationsEventsMapper compilationsEventsMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto dto) {

        if (compilationRepository.findByTitle(dto.getTitle()) != null) {
            throw new ApiError(
                    HttpStatus.CONFLICT,
                    "Integrity constraint has been violated.",
                    "could not execute statement; SQL [n/a]; constraint [uq_compilation_name]; " +
                            "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                            "could not execute statement");
        }

        Compilation compilation = new Compilation();
        compilation.setPinned(dto.isPinned());
        compilation.setTitle(dto.getTitle());

        Compilation savedComp = compilationRepository.save(compilation);

        List<Event> events = eventsRepository.findByIdIn(dto.getEvents());

        for (Event event : events) {
            CompilationEvents compilationEvent = new CompilationEvents();
            compilationEvent.setCompilationId(compilation.getId());
            compilationEvent.setEventId(event.getId());

            compilationEventsRepository.save(compilationEvent);
        }

        CompilationDto resultDto = new CompilationDto();
        resultDto.setId(savedComp.getId());
        resultDto.setPinned(savedComp.isPinned());
        resultDto.setTitle(savedComp.getTitle());
        resultDto.setEvents(events.stream()
                .map(compilationsEventsMapper::toDto
                )
                .toList()
        );

        return resultDto;
    }

    @Transactional
    @Override
    public void deleteCompilation(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new ApiError(
                    HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "Compilation with id=" + id + " was not found"
            );
        }
        compilationRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilationInfo(long id, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new ApiError(
                        HttpStatus.NOT_FOUND,
                        "The required object was not found.",
                        "Compilation with id=" + id + " was not found"
                ));

        //обновление инфо о подборке
        compilation.setPinned(request.isPinned());
        compilation.setTitle(request.getTitle());

        // Удаляем старые связи между Compilation и Event
        compilationEventsRepository.deleteByCompilationId(id);

        //ищем события по переданным id
        List<Event> events = eventsRepository.findByIdIn(request.getEvents());

        List<CompilationEvents> compilationEvents = new ArrayList<>();
        for (Event event : events) {
            CompilationEvents compilationEvent = new CompilationEvents();
            compilationEvent.setCompilationId(compilation.getId());
            compilationEvent.setEventId(event.getId());

            compilationEvents.add(compilationEvent);
        }

        compilationEventsRepository.saveAll(compilationEvents);

        Compilation savedComp = compilationRepository.save(compilation);

        CompilationDto resultDto = new CompilationDto();
        resultDto.setId(savedComp.getId());
        resultDto.setPinned(savedComp.isPinned());
        resultDto.setTitle(savedComp.getTitle());
        resultDto.setEvents(events.stream()
                .map(compilationsEventsMapper::toDto)
                .toList());

        return resultDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);

        // Логика получения списка подборок
        List<Compilation> compilations = compilationRepository.findByPinned(pinned);

        List<CompilationDto> compilationDtos = new ArrayList<>();

        for (Compilation compilation : compilations) {
            CompilationDto dto = new CompilationDto();
            dto.setId(compilation.getId());
            dto.setTitle(compilation.getTitle());
            dto.setPinned(compilation.isPinned());

            // Получаем события для текущей подборки
            List<Event> events = getEventsByCompilationId(compilation.getId(), pageRequest);

            dto.setEvents(events.stream()
                    .map(compilationsEventsMapper::toDto
                    )
                    .toList()
            );

            compilationDtos.add(dto);
        }

        return compilationDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationById(long compId) {

        if (!compilationRepository.existsById(compId)) {
            throw new ApiError(
                    HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "Compilation with id=" + compId + " was not found"
            );
        }

        Compilation compilation = compilationRepository.getReferenceById(compId);

        List<Event> events = getEventsByCompilationId(compId, PageRequest.of(0, 10));

        CompilationDto resultDto = new CompilationDto();

        resultDto.setId(compilation.getId());
        resultDto.setPinned(compilation.isPinned());
        resultDto.setTitle(compilation.getTitle());
        resultDto.setEvents(events.stream()
                .map(compilationsEventsMapper::toDto
                )
                .toList()
        );

        return resultDto;
    }

    @Transactional(readOnly = true)
    private List<Event> getEventsByCompilationId(long compilationId, PageRequest pageRequest) {
        // Получаем все записи, связывающие подборку и события
        List<CompilationEvents> compilationEvents = compilationEventsRepository.findByCompilationId(compilationId);

        // Извлекаем IDs событий
        List<Long> eventIds = compilationEvents.stream()
                .map(CompilationEvents::getEventId)
                .collect(Collectors.toList());

        // Получаем события по их ID с учетом пагинации
        Page<Event> eventsPage = eventsRepository.findAllById(eventIds, pageRequest);
        return eventsPage.getContent();
    }
}
