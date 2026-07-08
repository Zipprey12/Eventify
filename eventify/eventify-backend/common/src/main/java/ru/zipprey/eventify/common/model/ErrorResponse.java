package ru.zipprey.eventify.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String code;
    private Level level;
    private String message;
    private List<ErrorDetail> details;

    public String getLevel() {
        return level.getKey();
    }
}
