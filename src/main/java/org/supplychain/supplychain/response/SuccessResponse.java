package org.supplychain.supplychain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SuccessResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;
    private String path;

    public static <T> SuccessResponse<T> of(HttpStatus status, String message, T data, String path) {
        return SuccessResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .data(data)
                .path(path)
                .build();
    }
}