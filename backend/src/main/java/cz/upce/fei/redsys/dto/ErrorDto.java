package cz.upce.fei.redsys.dto;

import lombok.Builder;

import java.util.List;

public final class ErrorDto {
    private ErrorDto() {}

    @Builder
    public record ErrorResponse(
            int status,
            String error,
            String message,
            String path
    ) {}

    @Builder
    public record ValidationErrorResponse(
            int status,
            String error,
            String message,
            String path,
            List<String> validationErrors
    ) {}
}


