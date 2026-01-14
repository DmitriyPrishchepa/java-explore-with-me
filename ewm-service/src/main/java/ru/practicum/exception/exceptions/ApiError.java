package ru.practicum.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiError extends RuntimeException {
    private HttpStatus status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;

    public ApiError(HttpStatus status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
