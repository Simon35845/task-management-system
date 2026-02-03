package com.grapefruitapps.taskmanagementsystem.web;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String detailedMessage,
        LocalDateTime errorDateTime
) {
}
