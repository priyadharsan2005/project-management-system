
package com.example.project.management.controller;

import com.example.project.management.dto.ProjectDTO;
import com.example.project.management.entity.Project;
import com.example.project.management.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Create project
    @PostMapping
    public ProjectDTO createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    // Get all projects
    @GetMapping
    public List<ProjectDTO> getAllProjects() {
        return projectService.getAllProjects();
    }

    // Get project by ID
    @GetMapping("/{id}")
    public ProjectDTO getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    // Update project
    @PutMapping("/{id}")
    public ProjectDTO updateProject(
            @PathVariable Long id,
            @RequestBody Project project) {

        return projectService.updateProject(id, project);
    }

    // Delete project
    @DeleteMapping("/{id}")
    public String deleteProject(@PathVariable Long id) {

        projectService.deleteProject(id);

        return "Project deleted successfully";
    }

    // Assign manager
    @PutMapping("/{projectId}/manager/{userId}")
    public ProjectDTO assignManager(
            @PathVariable Long projectId,
            @PathVariable Long userId) {

        return projectService.assignManager(projectId, userId);
    }
}

