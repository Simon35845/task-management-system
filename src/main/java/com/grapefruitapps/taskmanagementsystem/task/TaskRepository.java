package com.grapefruitapps.taskmanagementsystem.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Transactional
    @Modifying
    @Query("update TaskEntity t set t.status = :status where t.id = :id")
    void setStatus(
            @Param("id") Long id,
            @Param("status") TaskStatus status);

    @Transactional
    @Modifying
    @Query("""
            update TaskEntity t
            set t.status = :status, t.doneDateTime = :doneDateTime
            where t.id = :id
            """)
    void setStatusAndDoneDateTime(
            @Param("id") Long id,
            @Param("status") TaskStatus status,
            @Param("doneDateTime") LocalDateTime doneDateTime);

    long countByStatus(TaskStatus status);
}
