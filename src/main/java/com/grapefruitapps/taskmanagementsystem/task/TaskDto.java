package com.grapefruitapps.taskmanagementsystem.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaskDto {
    private Long id;
    private Long creatorId;
    private Long assignedUserId;
    private TaskStatus status;
    private LocalDateTime createDateTime;
    private LocalDate deadlineDate;
    private TaskPriority priority;

    public TaskDto() {
    }

    public TaskDto(Long id,
                   Long creatorId,
                   Long assignedUserId,
                   TaskStatus status,
                   LocalDateTime createDateTime,
                   LocalDate deadlineDate,
                   TaskPriority priority) {
        this.id = id;
        this.creatorId = creatorId;
        this.assignedUserId = assignedUserId;
        this.status = status;
        this.createDateTime = createDateTime;
        this.deadlineDate = deadlineDate;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TaskDto taskDto = (TaskDto) object;
        return Objects.equals(id, taskDto.id) &&
                Objects.equals(creatorId, taskDto.creatorId) &&
                Objects.equals(assignedUserId, taskDto.assignedUserId) &&
                status == taskDto.status &&
                Objects.equals(createDateTime, taskDto.createDateTime) &&
                Objects.equals(deadlineDate, taskDto.deadlineDate) &&
                priority == taskDto.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creatorId, assignedUserId, status,
                createDateTime, deadlineDate, priority);
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", assignedUserId=" + assignedUserId +
                ", status=" + status +
                ", createDateTime=" + createDateTime +
                ", deadlineDate=" + deadlineDate +
                ", priority=" + priority +
                '}';
    }
}
