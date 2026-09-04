
package com.example.project.management.service;

import com.example.project.management.dto.ProjectDTO;
import com.example.project.management.dto.UserDTO;
import com.example.project.management.entity.Project;
import com.example.project.management.entity.User;
import com.example.project.management.exception.ResourceNotFoundException;
import com.example.project.management.repository.ProjectRepository;
import com.example.project.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository) {

        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // Create project
    public ProjectDTO createProject(Project project) {

        Project savedProject = projectRepository.save(project);

        return convertToDTO(savedProject);
    }

    // Get all projects
    public List<ProjectDTO> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Get project by ID
    public ProjectDTO getProjectById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        return convertToDTO(project);
    }

    // Update project
    public ProjectDTO updateProject(Long id, Project projectDetails) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setStartDate(projectDetails.getStartDate());
        project.setEndDate(projectDetails.getEndDate());
        project.setStatus(projectDetails.getStatus());

        Project updatedProject = projectRepository.save(project);

        return convertToDTO(updatedProject);
    }

    // Delete project
    public void deleteProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        projectRepository.delete(project);
    }

    // Assign manager
    public ProjectDTO assignManager(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        )
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        project.setManager(user);

        Project updatedProject = projectRepository.save(project);

        return convertToDTO(updatedProject);
    }

    // Convert Project Entity → ProjectDTO
    private ProjectDTO convertToDTO(Project project) {

        UserDTO managerDTO = null;

        if (project.getManager() != null) {

            User manager = project.getManager();

            managerDTO = new UserDTO(
                    manager.getId(),
                    manager.getName(),
                    manager.getEmail(),
                    manager.getRole()
            );
        }

        return new ProjectDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus(),
                managerDTO
        );
    }
}

