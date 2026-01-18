package ru.practicum.admin_api.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.compilations.CompilationDto;
import ru.practicum.dtos.compilations.NewCompilationDto;
import ru.practicum.dtos.compilations.UpdateCompilationRequest;
import ru.practicum.exception.exceptions.ApiError;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Validated
public class CompilationsController {
    private final CompilationsService service;

    @PostMapping
    public CompilationDto addCompilation(
            @RequestBody NewCompilationDto dto
    ) {
        if (dto.getTitle().isBlank()) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Field:title.Error:must not be blank.Value: " + dto.getTitle()
            );
        }

        return service.addCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(
            @PathVariable("compId") long compId
    ) {
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationInfo(
            @PathVariable("compId") long compId,
            @RequestBody UpdateCompilationRequest request
    ) {
        return service.updateCompilationInfo(compId, request);
    }
}
