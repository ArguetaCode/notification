package com.campusenroll.notification.exception;

import java.time.OffsetDateTime;

public class ErrorResponse {

    private final OffsetDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;

    public ErrorResponse(OffsetDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
