
package com.example.project.management.service;

import java.util.List;
import java.util.Set;

import com.example.project.management.dto.ProjectDTO;
import com.example.project.management.dto.TaskDTO;
import com.example.project.management.dto.UserDTO;
import com.example.project.management.entity.Project;
import com.example.project.management.entity.Task;
import com.example.project.management.entity.User;
import com.example.project.management.exception.ResourceNotFoundException;
import com.example.project.management.repository.ProjectRepository;
import com.example.project.management.repository.TaskRepository;
import com.example.project.management.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private static final Set<String> VALID_STATUSES =
            Set.of("TODO", "IN_PROGRESS", "COMPLETED");

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CREATE TASK
    // =========================================================

    public TaskDTO createTask(Task task) {

        validateStatus(task.getStatus());

        Task savedTask = taskRepository.save(task);

        return convertToDTO(savedTask);
    }

    // =========================================================
    // GET TASKS
    // FILTER + SEARCH + PAGINATION + SORTING
    // =========================================================

    public Page<TaskDTO> getAllTasks(
            String status,
            String title,
            int page,
            int size,
            String sortBy,
            String direction) {

        // -----------------------------------------------------
        // Validate page
        // -----------------------------------------------------

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        // -----------------------------------------------------
        // Validate size
        // -----------------------------------------------------

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        // -----------------------------------------------------
        // Allowed sorting fields
        // -----------------------------------------------------

        Set<String> allowedSortFields = Set.of(
                "id",
                "title",
                "status",
                "dueDate"
        );

        if (!allowedSortFields.contains(sortBy)) {

            throw new IllegalArgumentException(
                    "Invalid sort field. Allowed values: "
                            + "id, title, status, dueDate"
            );
        }

        // -----------------------------------------------------
        // Sort direction
        // -----------------------------------------------------

        Sort.Direction sortDirection;

        try {

            sortDirection =
                    Sort.Direction.fromString(direction);

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Invalid sort direction. Use 'asc' or 'desc'"
            );
        }

        // -----------------------------------------------------
        // Create Pageable
        // -----------------------------------------------------

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortDirection, sortBy)
                );

        // -----------------------------------------------------
        // Get logged-in user
        // -----------------------------------------------------

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        // -----------------------------------------------------
        // Check Manager role
        // -----------------------------------------------------

        boolean isManager =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_MANAGER")
                        );

        Page<Task> tasks;

        // =====================================================
        // MANAGER
        // =====================================================

        if (isManager) {

            // ---------------------------------------------
            // Status + title
            // ---------------------------------------------

            if (status != null && !status.isBlank()
                    && title != null && !title.isBlank()) {

                tasks = taskRepository
                        .findByStatus(
                                status,
                                pageable
                        );

                List<Task> filteredTasks =
                        tasks.getContent()
                                .stream()
                                .filter(task ->
                                        task.getTitle() != null
                                                &&
                                        task.getTitle()
                                                .toLowerCase()
                                                .contains(
                                                        title.toLowerCase()
                                                )
                                )
                                .toList();

                tasks =
                        new org.springframework.data.domain.PageImpl<>(
                                filteredTasks,
                                pageable,
                                filteredTasks.size()
                        );
            }

            // ---------------------------------------------
            // Status only
            // ---------------------------------------------

            else if (status != null && !status.isBlank()) {

                tasks =
                        taskRepository.findByStatus(
                                status,
                                pageable
                        );
            }

            // ---------------------------------------------
            // Title only
            // ---------------------------------------------

            else if (title != null && !title.isBlank()) {

                tasks =
                        taskRepository
                                .findByTitleContainingIgnoreCase(
                                        title,
                                        pageable
                                );
            }

            // ---------------------------------------------
            // No filter
            // ---------------------------------------------

            else {

                tasks =
                        taskRepository.findAll(pageable);
            }
        }

        // =====================================================
        // EMPLOYEE
        // =====================================================

        else {

            User user =
                    userRepository.findByEmail(email)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found with email: "
                                                    + email
                                    )
                            );

            // ---------------------------------------------
            // Status + title
            // ---------------------------------------------

            if (status != null && !status.isBlank()
                    && title != null && !title.isBlank()) {

                tasks =
                        taskRepository
                                .findByAssignedUserIdAndStatus(
                                        user.getId(),
                                        status,
                                        pageable
                                );

                List<Task> filteredTasks =
                        tasks.getContent()
                                .stream()
                                .filter(task ->
                                        task.getTitle() != null
                                                &&
                                        task.getTitle()
                                                .toLowerCase()
                                                .contains(
                                                        title.toLowerCase()
                                                )
                                )
                                .toList();

                tasks =
                        new org.springframework.data.domain.PageImpl<>(
                                filteredTasks,
                                pageable,
                                filteredTasks.size()
                        );
            }

            // ---------------------------------------------
            // Status only
            // ---------------------------------------------

            else if (status != null && !status.isBlank()) {

                tasks =
                        taskRepository
                                .findByAssignedUserIdAndStatus(
                                        user.getId(),
                                        status,
                                        pageable
                                );
            }

            // ---------------------------------------------
            // Title only
            // ---------------------------------------------

            else if (title != null && !title.isBlank()) {

                tasks =
                        taskRepository
                                .findByAssignedUserIdAndTitleContainingIgnoreCase(
                                        user.getId(),
                                        title,
                                        pageable
                                );
            }

            // ---------------------------------------------
            // No filter
            // ---------------------------------------------

            else {

                tasks =
                        taskRepository.findByAssignedUserId(
                                user.getId(),
                                pageable
                        );
            }
        }

        // -----------------------------------------------------
        // Convert Page<Task> → Page<TaskDTO>
        // -----------------------------------------------------

        return tasks.map(this::convertToDTO);
    }

    // =========================================================
    // GET TASK BY ID
    // =========================================================

    public TaskDTO getTaskById(Long id) {

        Task task =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found with id: " + id
                                )
                        );

        return convertToDTO(task);
    }

    // =========================================================
    // UPDATE TASK
    // =========================================================

    public TaskDTO updateTask(
            Long id,
            Task taskDetails) {

        Task task =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found with id: " + id
                                )
                        );

        // Validate status
        validateStatus(taskDetails.getStatus());

        // Get logged-in user
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        // Check Manager
        boolean isManager =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_MANAGER")
                        );

        // =====================================================
        // MANAGER
        // =====================================================

        if (isManager) {

            task.setTitle(taskDetails.getTitle());

            task.setDescription(
                    taskDetails.getDescription()
            );

            task.setStatus(
                    taskDetails.getStatus()
            );

            task.setDueDate(
                    taskDetails.getDueDate()
            );
        }

        // =====================================================
        // EMPLOYEE
        // =====================================================

        else {

            if (task.getAssignedUser() == null
                    ||
                    !task.getAssignedUser()
                            .getEmail()
                            .equals(email)) {

                throw new org.springframework.security.access
                        .AccessDeniedException(
                                "You can update only your own assigned tasks"
                        );
            }

            // Employee can ONLY update status
            task.setStatus(
                    taskDetails.getStatus()
            );
        }

        Task updatedTask =
                taskRepository.save(task);

        return convertToDTO(updatedTask);
    }

    // =========================================================
    // DELETE TASK
    // =========================================================

    public void deleteTask(Long id) {

        Task task =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found with id: " + id
                                )
                        );

        taskRepository.delete(task);
    }

    // =========================================================
    // ASSIGN TASK TO PROJECT
    // =========================================================

    public TaskDTO assignProject(
            Long taskId,
            Long projectId) {

        Task task =
                taskRepository.findById(taskId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found with id: "
                                                + taskId
                                )
                        );

        Project project =
                projectRepository.findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found with id: "
                                                + projectId
                                )
                        );

        task.setProject(project);

        Task updatedTask =
                taskRepository.save(task);

        return convertToDTO(updatedTask);
    }

    // =========================================================
    // ASSIGN TASK TO USER
    // =========================================================

    public TaskDTO assignUser(
            Long taskId,
            Long userId) {

        Task task =
                taskRepository.findById(taskId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found with id: "
                                                + taskId
                                )
                        );

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                        );

        task.setAssignedUser(user);

        Task updatedTask =
                taskRepository.save(task);

        return convertToDTO(updatedTask);
    }

    // =========================================================
    // VALIDATE TASK STATUS
    // =========================================================

    private void validateStatus(String status) {

        if (status == null
                || !VALID_STATUSES.contains(status)) {

            throw new IllegalArgumentException(
                    "Invalid task status. "
                            + "Allowed values: "
                            + "TODO, IN_PROGRESS, COMPLETED"
            );
        }
    }

    // =========================================================
    // CONVERT ENTITY → DTO
    // =========================================================

    private TaskDTO convertToDTO(Task task) {

        // -----------------------------------------------------
        // Assigned User
        // -----------------------------------------------------

        UserDTO assignedUserDTO = null;

        if (task.getAssignedUser() != null) {

            User user =
                    task.getAssignedUser();

            assignedUserDTO =
                    new UserDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole()
                    );
        }

        // -----------------------------------------------------
        // Project
        // -----------------------------------------------------

        ProjectDTO projectDTO = null;

        if (task.getProject() != null) {

            Project project =
                    task.getProject();

            UserDTO managerDTO = null;

            // ---------------------------------------------
            // Project Manager
            // ---------------------------------------------

            if (project.getManager() != null) {

                User manager =
                        project.getManager();

                managerDTO =
                        new UserDTO(
                                manager.getId(),
                                manager.getName(),
                                manager.getEmail(),
                                manager.getRole()
                        );
            }

            projectDTO =
                    new ProjectDTO(
                            project.getId(),
                            project.getName(),
                            project.getDescription(),
                            project.getStartDate(),
                            project.getEndDate(),
                            project.getStatus(),
                            managerDTO
                    );
        }

        // -----------------------------------------------------
        // Task DTO
        // -----------------------------------------------------

        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                projectDTO,
                assignedUserDTO
        );
    }
}

