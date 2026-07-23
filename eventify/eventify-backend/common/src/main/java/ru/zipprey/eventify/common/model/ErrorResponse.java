package ru.zipprey.eventify.common.model;

import java.util.List;

public record ErrorResponse(String code, Level level, String message, List<ErrorDetail> details) {
}
