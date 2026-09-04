
package com.example.project.management.controller;

import com.example.project.management.dto.TaskDTO;
import com.example.project.management.entity.Task;
import com.example.project.management.service.TaskService;

import org.springframework.data.domain.Page;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // =========================================================
    // CREATE TASK
    // =========================================================

    @PostMapping
    public TaskDTO createTask(
            @RequestBody Task task) {

        return taskService.createTask(task);
    }

    // =========================================================
    // GET TASKS
    // FILTER + SEARCH + PAGINATION + SORTING
    // =========================================================

    @GetMapping
    public Page<TaskDTO> getAllTasks(

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            String title,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        return taskService.getAllTasks(
                status,
                title,
                page,
                size,
                sortBy,
                direction
        );
    }

    // =========================================================
    // GET TASK BY ID
    // =========================================================

    @GetMapping("/{id}")
    public TaskDTO getTaskById(
            @PathVariable Long id) {

        return taskService.getTaskById(id);
    }

    // =========================================================
    // UPDATE TASK
    // =========================================================

    @PutMapping("/{id}")
    public TaskDTO updateTask(
            @PathVariable Long id,
            @RequestBody Task task) {

        return taskService.updateTask(
                id,
                task
        );
    }

    // =========================================================
    // DELETE TASK
    // =========================================================

    @DeleteMapping("/{id}")
    public String deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);

        return "Task deleted successfully";
    }

    // =========================================================
    // ASSIGN TASK TO PROJECT
    // =========================================================

    @PutMapping("/{taskId}/project/{projectId}")
    public TaskDTO assignProject(
            @PathVariable Long taskId,
            @PathVariable Long projectId) {

        return taskService.assignProject(
                taskId,
                projectId
        );
    }

    // =========================================================
    // ASSIGN TASK TO USER
    // =========================================================

    @PutMapping("/{taskId}/user/{userId}")
    public TaskDTO assignUser(
            @PathVariable Long taskId,
            @PathVariable Long userId) {

        return taskService.assignUser(
                taskId,
                userId
        );
    }
}

