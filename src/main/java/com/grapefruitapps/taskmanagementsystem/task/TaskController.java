package com.grapefruitapps.taskmanagementsystem.task;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO добавить обработку PUT, DELETE запросов
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
    public ResponseEntity< List<TaskDto>> getAllTasks() {
        log.info("Called getAllTasks");
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable Long id
    ) {
        log.info("Called getTaskById: id={}", id);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity< TaskDto> createTask(
            @RequestBody TaskDto taskDto
    ){
        log.info("Called createTask");
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(taskService.createTask(taskDto));
    }
}
