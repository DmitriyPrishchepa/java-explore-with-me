package ru.practicum.public_api.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin_api.compilations.CompilationsService;
import ru.practicum.dtos.compilations.CompilationDto;
import ru.practicum.exception.exceptions.ApiError;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationControllerPublic {
    private final CompilationsService service;

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam("pinned") boolean pinned,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            int uId = Integer.parseInt(String.valueOf(from));
            int eId = Integer.parseInt(String.valueOf(size));

            return service.getCompilations(pinned, uId, eId);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(
            @PathVariable("compId") long compId
    ) {
        try {
            int id = Integer.parseInt(String.valueOf(compId));

            return service.getCompilationById(id);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }
}
