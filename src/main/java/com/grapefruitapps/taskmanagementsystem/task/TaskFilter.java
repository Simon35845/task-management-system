package com.grapefruitapps.taskmanagementsystem.task;

public record TaskFilter (
        Long creatorId,
        Long assignedUserId,
        TaskStatus status,
        TaskPriority priority,
        Integer pageSize,
        Integer pageNumber
) {}