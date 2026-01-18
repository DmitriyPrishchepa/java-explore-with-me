package ru.practicum.admin_api.compilations;

import ru.practicum.dtos.compilations.CompilationDto;
import ru.practicum.dtos.compilations.NewCompilationDto;
import ru.practicum.dtos.compilations.UpdateCompilationRequest;

public interface CompilationsService {
    CompilationDto addCompilation(NewCompilationDto dto);

    void deleteCompilation(long id);

    CompilationDto updateCompilationInfo(long id, UpdateCompilationRequest request);

}
