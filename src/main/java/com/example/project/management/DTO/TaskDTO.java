
package com.example.project.management.dto;

import java.time.LocalDate;

public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;

    private ProjectDTO project;
    private UserDTO assignedUser;

    public TaskDTO() {
    }

    public TaskDTO(
            Long id,
            String title,
            String description,
            String status,
            LocalDate dueDate,
            ProjectDTO project,
            UserDTO assignedUser) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.project = project;
        this.assignedUser = assignedUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public UserDTO getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(UserDTO assignedUser) {
        this.assignedUser = assignedUser;
    }
}

