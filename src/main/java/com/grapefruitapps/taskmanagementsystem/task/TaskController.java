package com.grapefruitapps.taskmanagementsystem.task;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//TODO добавить обработку POST, PUT, DELETE запросов
//TODO реализовать перехватывание исключений
//TODO реализовать валидацию входных данных

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping()
    public List<TaskDto> getAllTasks() {
        log.info("Called getAllTasks");
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(
            @PathVariable Long id
    ) {
        log.info("Called getTaskById: id={}", id);
        return taskService.getTaskById(id);
    }
}
