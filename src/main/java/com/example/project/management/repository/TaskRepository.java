
package com.example.project.management.repository;

import com.example.project.management.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Employee → only assigned tasks
    Page<Task> findByAssignedUserId(
            Long userId,
            Pageable pageable
    );

    // Filter by status
    Page<Task> findByStatus(
            String status,
            Pageable pageable
    );

    // Search by title
    Page<Task> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );

    // Employee + status filter
    Page<Task> findByAssignedUserIdAndStatus(
            Long userId,
            String status,
            Pageable pageable
    );

    // Employee + title search
    Page<Task> findByAssignedUserIdAndTitleContainingIgnoreCase(
            Long userId,
            String title,
            Pageable pageable
    );
}
