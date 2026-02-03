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
        validation(taskToCreate);
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

    public TaskDto updateTask(Long id, TaskDto taskToUpdate) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task by id = " + id);
        }
        validation(taskToUpdate);

        TaskDto updatedTask = taskMap.get(id);
        if (updatedTask.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot modify task: status = " + updatedTask.getStatus());
        }

        if (taskToUpdate.getDeadlineDate().isBefore(updatedTask.getCreateDateTime().toLocalDate())) {
            throw new IllegalArgumentException("Task deadline date must be after task creation date");
        }

        updatedTask.setCreatorId(taskToUpdate.getCreatorId());
        updatedTask.setAssignedUserId(taskToUpdate.getAssignedUserId());
        updatedTask.setDeadlineDate(taskToUpdate.getDeadlineDate());

        if (taskToUpdate.getPriority() != null) {
            updatedTask.setPriority(taskToUpdate.getPriority());
        }

        taskMap.put(id, updatedTask);
        return updatedTask;
    }


    public void deleteTask(Long id) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task by id = " + id);
        }
        taskMap.remove(id);
    }

    public TaskDto changeTaskStatus(Long id, TaskStatus status) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task by id = " + id);
        }
        TaskDto updatedTask = taskMap.get(id);
        updatedTask.setStatus(status);
        taskMap.put(id, updatedTask);
        return updatedTask;
    }

    private void validation(TaskDto taskDto) {
        if (taskDto.getId() != null) {
            throw new IllegalArgumentException("Task id must be empty");
        }
        if (taskDto.getCreatorId() == null) {
            throw new IllegalArgumentException("Task creator must exist");
        }
        if (taskDto.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Task executor must exist");
        }
        if (taskDto.getStatus() != null) {
            throw new IllegalArgumentException("Task status must be empty");
        }
        if (taskDto.getCreateDateTime() != null) {
            throw new IllegalArgumentException("Task creation date and time must be empty");
        }
        if (taskDto.getDeadlineDate() == null) {
            throw new IllegalArgumentException("Task deadline date must be specified");
        }
    }
}
