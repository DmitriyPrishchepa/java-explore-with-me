package ru.practicum.admin_api.compilations;

import ru.practicum.dtos.compilations.CompilationDto;
import ru.practicum.dtos.compilations.NewCompilationDto;
import ru.practicum.dtos.compilations.UpdateCompilationRequest;

import java.util.List;

public interface CompilationsService {
    CompilationDto addCompilation(NewCompilationDto dto);

    void deleteCompilation(long id);

    CompilationDto updateCompilationInfo(long id, UpdateCompilationRequest request);

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);

    CompilationDto getCompilationById(long compId);
}
