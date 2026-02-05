package com.grapefruitapps.taskmanagementsystem.task;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    public static final int MAX_COUNT_OF_TASKS_IN_PROGRESS = 5;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<TaskDto> getAllTasks() {
        log.debug("Get all tasks");
        List<TaskEntity> taskEntities = repository.findAll();
        log.debug("Found {} tasks ", taskEntities.size());
        return taskEntities.stream().map(mapper::toDto).toList();
    }

    public TaskDto getTaskById(Long id) {
        log.debug("Get task by id: {}", id);
        TaskEntity fetchedEntity = fetchEntityById(id);
        log.debug("Found task with id: {}", id);
        return mapper.toDto(fetchedEntity);
    }

    public TaskDto createTask(TaskDto taskDto) {
        log.info("Create new task with id: {}", taskDto.getId());
        validation(taskDto);

        if (taskDto.getDeadlineDate() != null &&
                taskDto.getDeadlineDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Task deadline date must be after task creation date: "
                    + LocalDate.now());
        }

        TaskEntity entityToSave = mapper.toEntity(taskDto);
        entityToSave.setStatus(TaskStatus.CREATED);
        entityToSave.setCreateDateTime(LocalDateTime.now());

        if (entityToSave.getPriority() == null) {
            entityToSave.setPriority(TaskPriority.MEDIUM);
        }

        TaskEntity savedEntity = repository.save(entityToSave);
        log.info("Task created successfully, id: {}", savedEntity.getId());
        return mapper.toDto(savedEntity);
    }

    public TaskDto updateTask(Long id, TaskDto taskDto) {
        log.info("Update task with id: {}", taskDto.getId());
        validation(taskDto);
        TaskEntity fetchedEntity = fetchEntityById(id);

        if (fetchedEntity.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot modify task: status = " + fetchedEntity.getStatus());
        }

        if (fetchedEntity.getStatus() == TaskStatus.IN_PROGRESS &&
                taskDto.getAssignedUserId() == null) {
            throw new IllegalStateException("Task with status: "
                    + fetchedEntity.getStatus() + " must have an executor");
        }

        if (taskDto.getDeadlineDate() != null &&
                taskDto.getDeadlineDate().isBefore(fetchedEntity.getCreateDateTime().toLocalDate())) {
            throw new IllegalArgumentException("Task deadline date must be after task creation date: "
                    + fetchedEntity.getCreateDateTime().toLocalDate());
        }

        TaskEntity entityToUpdate = mapper.toEntity(taskDto);
        entityToUpdate.setId(fetchedEntity.getId());
        entityToUpdate.setStatus(fetchedEntity.getStatus());
        entityToUpdate.setCreateDateTime(fetchedEntity.getCreateDateTime());

        if (entityToUpdate.getPriority() == null) {
            entityToUpdate.setPriority(fetchedEntity.getPriority());
        }

        TaskEntity updatedEntity = repository.save(entityToUpdate);
        log.info("Task updated successfully, id: {}", updatedEntity.getId());
        return mapper.toDto(updatedEntity);
    }

    public void deleteTask(Long id) {
        log.info("Delete task with id: {}", id);
        if (!repository.existsById(id)) {
            log.warn("Task with id {} not found in database", id);
            throw new EntityNotFoundException("Not found task by id = " + id);
        }
        repository.deleteById(id);
        log.info("Task deleted, id: {}", id);
    }

    public TaskDto startTask(Long id) {
        log.info("Start task with id: {}", id);
        TaskEntity fetchedEntity = fetchEntityById(id);

        if (fetchedEntity.getStatus() != TaskStatus.CREATED) {
            throw new IllegalStateException("Cannot start task: status = " + fetchedEntity.getStatus());
        }

        if (fetchedEntity.getAssignedUserId() == null) {
            throw new IllegalStateException("Task must have an executor");
        }

        if (getTasksByStatus(TaskStatus.IN_PROGRESS).size() > MAX_COUNT_OF_TASKS_IN_PROGRESS) {
            throw new IllegalStateException("Count of tasks with status = " + fetchedEntity.getStatus()
                    + " must be less than " + MAX_COUNT_OF_TASKS_IN_PROGRESS);
        }

        repository.setStatus(id, TaskStatus.IN_PROGRESS);
        fetchedEntity.setStatus(TaskStatus.IN_PROGRESS);
        log.info("Task started, id: {}", id);
        return mapper.toDto(fetchedEntity);
    }

    public TaskDto approveTask(Long id) {
        log.info("Approve task with id: {}", id);
        TaskEntity fetchedEntity = fetchEntityById(id);

        if (fetchedEntity.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot approve task: status = " + fetchedEntity.getStatus());
        }

        repository.setStatus(id, TaskStatus.DONE);
        fetchedEntity.setStatus(TaskStatus.DONE);
        log.info("Task approved, id: {}", id);
        return mapper.toDto(fetchedEntity);
    }

    public TaskDto resumeTask(Long id) {
        log.info("Resume task with id: {}", id);
        TaskEntity fetchedEntity = fetchEntityById(id);

        if (fetchedEntity.getStatus() != TaskStatus.DONE) {
            throw new IllegalStateException("Cannot resume task: status = " + fetchedEntity.getStatus());
        }

        repository.setStatus(id, TaskStatus.IN_PROGRESS);
        fetchedEntity.setStatus(TaskStatus.IN_PROGRESS);
        log.info("Task resumed, id: {}", id);
        return mapper.toDto(fetchedEntity);
    }

    private TaskEntity fetchEntityById(Long id) {
        log.debug("Fetch task entity by id: {}", id);
        return repository.findById(id).orElseThrow(
                () -> {
                    log.warn("Task with id {} not found in database", id);
                    return new EntityNotFoundException("Not found task by id = " + id);
                });
    }

    private void validation(TaskDto taskDto) {
        log.debug("Validate task DTO with id: {}", taskDto.getId());
        if (taskDto.getId() != null) {
            throw new IllegalArgumentException("Task id must be empty");
        }
        if (taskDto.getCreatorId() == null) {
            throw new IllegalArgumentException("Task creator must exist");
        }
        if (taskDto.getStatus() != null) {
            throw new IllegalArgumentException("Task status must be empty");
        }
        if (taskDto.getCreateDateTime() != null) {
            throw new IllegalArgumentException("Task creation date and time must be empty");
        }
        log.debug("Task DTO validation passed, id: {}", taskDto.getId());
    }

    private List<TaskEntity> getTasksByStatus(TaskStatus status) {
        log.debug("Get tasks with status: {}", status);
        return repository.findByStatus(status);
    }
}
