package com.grapefruitapps.taskmanagementsystem.task;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    public static final int MAX_COUNT_OF_TASKS_IN_PROGRESS = 5;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<TaskDto> searchAllByFilter(TaskFilter filter) {
        log.debug("Get all tasks by filter");
        int pageSize = filter.pageSize() != null ? filter.pageSize() : DEFAULT_PAGE_SIZE;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : DEFAULT_PAGE_NUMBER;
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<TaskEntity> taskEntities = repository.searchAllByFilter(
                filter.creatorId(),
                filter.assignedUserId(),
                filter.status(),
                filter.priority(),
                pageable
        );
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

        if (repository.countByStatus(TaskStatus.IN_PROGRESS) > MAX_COUNT_OF_TASKS_IN_PROGRESS) {
            throw new IllegalStateException("Count of tasks with status = " + fetchedEntity.getStatus()
                    + " must be less than " + MAX_COUNT_OF_TASKS_IN_PROGRESS);
        }

        repository.setStatus(id, TaskStatus.IN_PROGRESS);
        fetchedEntity.setStatus(TaskStatus.IN_PROGRESS);
        log.info("Task started, id: {}", id);
        return mapper.toDto(fetchedEntity);
    }

    public TaskDto completeTask(Long id) {
        log.info("Complete task with id: {}", id);
        TaskEntity fetchedEntity = fetchEntityById(id);

        if (fetchedEntity.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete task: status = " + fetchedEntity.getStatus());
        }

        LocalDateTime doneDateTime = LocalDateTime.now();
        repository.setStatusAndDoneDateTime(id, TaskStatus.DONE, doneDateTime);
        fetchedEntity.setStatus(TaskStatus.DONE);
        fetchedEntity.setDoneDateTime(doneDateTime);
        log.info("Task completed, id: {}", id);
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
}
