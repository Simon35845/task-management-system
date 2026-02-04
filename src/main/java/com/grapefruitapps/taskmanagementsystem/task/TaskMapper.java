package com.grapefruitapps.taskmanagementsystem.task;

import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskDto toDto(TaskEntity entity){
        return new TaskDto(
                entity.getId(),
                entity.getCreatorId(),
                entity.getAssignedUserId(),
                entity.getStatus(),
                entity.getCreateDateTime(),
                entity.getDeadlineDate(),
                entity.getPriority()
        );
    }

    public TaskEntity toEntity(TaskDto dto){
        return new TaskEntity(
                dto.getId(),
                dto.getCreatorId(),
                dto.getAssignedUserId(),
                dto.getStatus(),
                dto.getCreateDateTime(),
                dto.getDeadlineDate(),
                dto.getPriority()
        );
    }
}
