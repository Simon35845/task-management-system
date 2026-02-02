package com.grapefruitapps.taskmanagementsystem.task;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

//TODO реализовать взаимодействие с БД через объект Repository

@Service
public class TaskService {
    //TODO убрать мапу-заглушку
    private final Map<Long, TaskDto> taskMap = new TreeMap<>();

    {
        taskMap.put(1L,
                new TaskDto(1L, 2L, 12L,
                        TaskStatus.CREATED,
                        LocalDateTime.now(),
                        LocalDate.now().plusDays(50),
                        TaskPriority.HIGH));

        taskMap.put(2L,
                new TaskDto(2L, 4L, 12L,
                        TaskStatus.IN_PROGRESS,
                        LocalDateTime.now().minusDays(12),
                        LocalDate.now().plusDays(40),
                        TaskPriority.MEDIUM));
        taskMap.put(3L,
                new TaskDto(3L, 1L, 2L,
                        TaskStatus.IN_PROGRESS,
                        LocalDateTime.now().minusDays(11),
                        LocalDate.now().plusDays(32),
                        TaskPriority.LOW));
        taskMap.put(4L,
                new TaskDto(4L, 2L, 5L,
                        TaskStatus.DONE,
                        LocalDateTime.now().minusDays(26),
                        LocalDate.now().minusDays(6),
                        TaskPriority.HIGH));
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

}
