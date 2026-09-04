
package com.example.project.management.service;

import com.example.project.management.dto.DashboardDTO;
import com.example.project.management.entity.Task;
import com.example.project.management.repository.ProjectRepository;
import com.example.project.management.repository.TaskRepository;
import com.example.project.management.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public DashboardService(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            UserRepository userRepository) {

        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET DASHBOARD STATISTICS
    // =========================================================

    public DashboardDTO getDashboard() {

        // Total projects
        long totalProjects =
                projectRepository.count();

        // Get all tasks
        List<Task> tasks =
                taskRepository.findAll();

        // Total tasks
        long totalTasks =
                tasks.size();

        // TODO tasks
        long todoTasks =
                tasks.stream()
                        .filter(task ->
                                "TODO".equals(task.getStatus()))
                        .count();

        // IN_PROGRESS tasks
        long inProgressTasks =
                tasks.stream()
                        .filter(task ->
                                "IN_PROGRESS".equals(task.getStatus()))
                        .count();

        // COMPLETED tasks
        long completedTasks =
                tasks.stream()
                        .filter(task ->
                                "COMPLETED".equals(task.getStatus()))
                        .count();

        // Total employees
        long totalEmployees =
                userRepository.findAll()
                        .stream()
                        .filter(user ->
                                "EMPLOYEE".equals(user.getRole()))
                        .count();

        // Return dashboard data
        return new DashboardDTO(
                totalProjects,
                totalTasks,
                todoTasks,
                inProgressTasks,
                completedTasks,
                totalEmployees
        );
    }
}

