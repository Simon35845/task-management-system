package com.grapefruitapps.taskmanagementsystem.task;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping()
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam(name = "creatorId", required = false) Long creatorId,
            @RequestParam(name = "assignedUserId", required = false) Long assignedUserId,
            @RequestParam(name = "status", required = false) TaskStatus status,
            @RequestParam(name = "priority", required = false) TaskPriority priority,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        TaskFilter filter = new TaskFilter(
                creatorId,
                assignedUserId,
                status,
                priority,
                pageSize,
                pageNumber
        );
        log.info("Called getAllTasks");
        return ResponseEntity.ok(taskService.searchAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable Long id
    ) {
        log.info("Called getTaskById: id={}", id);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestBody @Valid TaskDto taskDto
    ) {
        log.info("Called createTask");
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskDto taskDto
    ) {
        log.info("Called updateTask: id={}, task={}", id, taskDto);
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id
    ) {
        log.info("Called deleteTask: id={}", id);
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<TaskDto> startTask(
            @PathVariable Long id
    ){
        log.info("Called startTask: id={}", id);
        TaskDto startedTask = taskService.startTask(id);
        return ResponseEntity.ok(startedTask);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskDto> completeTask(
            @PathVariable Long id
    ){
        log.info("Called completeTask: id={}", id);
        TaskDto completedTask = taskService.completeTask(id);
        return ResponseEntity.ok(completedTask);
    }

    @PatchMapping("/{id}/resume")
    public ResponseEntity<TaskDto> resumeTask(
            @PathVariable Long id
    ){
        log.info("Called resumeTask: id={}", id);
        TaskDto resumedTask = taskService.resumeTask(id);
        return ResponseEntity.ok(resumedTask);
    }
}
