package ru.practicum.admin_api.users.validation;

import org.springframework.http.HttpStatus;
import ru.practicum.admin_api.users.model.NewUserRequest;
import ru.practicum.exception.exceptions.ApiError;

public class UserValidator {
    public static void validateUser(NewUserRequest request) {
        if (request.getName().isBlank()) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Field: name. Error: must not be blank. Value: null"
            );
        }
        if (request.getEmail().isBlank()) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Field: email. Error: must not be blank. Value: null"
            );
        }
    }
}
