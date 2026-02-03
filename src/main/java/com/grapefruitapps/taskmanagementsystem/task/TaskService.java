package com.grapefruitapps.taskmanagementsystem.task;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

//TODO реализовать взаимодействие с БД через объект Repository

@Service
public class TaskService {
    //TODO убрать мапу-заглушку
    private final Map<Long, TaskDto> taskMap;
    private final AtomicLong idCounter;

    public TaskService() {
        taskMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public List<TaskDto> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    public TaskDto getTaskById(Long id) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task by id = " + id);
        }
        return taskMap.get(id);
    }

    public TaskDto createTask(TaskDto taskToCreate) {
        if (taskToCreate.getId() != null) {
            throw new IllegalArgumentException("Task id must be empty");
        }
        if (taskToCreate.getCreatorId() == null) {
            throw new IllegalArgumentException("Task creator must exist");
        }
        if (taskToCreate.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Task executor must exist");
        }
        if (taskToCreate.getStatus() != null) {
            throw new IllegalArgumentException("Task status must be empty");
        }
        if (taskToCreate.getCreateDateTime() != null) {
            throw new IllegalArgumentException("Task creation date and time must be empty");
        }
        if (taskToCreate.getDeadlineDate() == null) {
            throw new IllegalArgumentException("Task deadline date must be specified");
        }
        if (taskToCreate.getDeadlineDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Task deadline date must be after task creation date");
        }
        TaskPriority priority = taskToCreate.getPriority() != null
                ? taskToCreate.getPriority()
                : TaskPriority.MEDIUM;

        TaskDto newTask = new TaskDto(
                idCounter.incrementAndGet(),
                taskToCreate.getCreatorId(),
                taskToCreate.getAssignedUserId(),
                TaskStatus.CREATED,
                LocalDateTime.now(),
                taskToCreate.getDeadlineDate(),
                priority
        );

        taskMap.put(newTask.getId(), newTask);
        return newTask;
    }
}
