import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { ProjectService, Project } from '../services/project.service';
import { UserService, User } from '../services/user.service';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './projects.html',
  styleUrl: './projects.css'
})
export class Projects implements OnInit {

  // =========================
  // PROJECT DATA
  // =========================

  projects: Project[] = [];

  // Users are still used technically.
  // In the UI they will be displayed as Employees.
  users: User[] = [];

  selectedManagerId: number | null = null;


  // =========================
  // PROJECT FORM
  // =========================

  project: Project = {
    name: '',
    description: '',
    status: 'ACTIVE'
  };


  // =========================
  // STATE
  // =========================

  editing = false;
  editingId: number | null = null;

  loading = false;
  saving = false;

  errorMessage = '';
  successMessage = '';


  // =========================
  // CONSTRUCTOR
  // =========================

  constructor(
    private projectService: ProjectService,
    private userService: UserService
  ) {}


  // =========================
  // INITIAL LOAD
  // =========================

  ngOnInit(): void {

    this.loadProjects();

    this.loadUsers();

  }


  // =========================
  // LOAD PROJECTS
  // =========================

  loadProjects(): void {

    this.loading = true;

    this.errorMessage = '';

    this.projectService.getProjects().subscribe({

      next: (data: Project[]) => {

        this.projects = data;

        this.loading = false;

      },

      error: (error: HttpErrorResponse) => {

        console.error(
          'PROJECT LOAD ERROR:',
          error
        );

        this.errorMessage =
          'Failed to load projects';

        this.loading = false;

      }

    });
  }


  // =========================
  // LOAD USERS / EMPLOYEES
  // =========================

  loadUsers(): void {

    this.userService.getUsers().subscribe({

      next: (data: User[]) => {

        this.users = data;

      },

      error: (error: HttpErrorResponse) => {

        console.error(
          'USER LOAD ERROR:',
          error
        );

        this.errorMessage =
          'Failed to load employees';

      }

    });
  }


  // =========================
  // SAVE PROJECT
  // =========================

  saveProject(): void {

    this.errorMessage = '';

    this.successMessage = '';


    // Validate project name

    if (!this.project.name?.trim()) {

      this.errorMessage =
        'Project name is required';

      return;

    }


    // Validate manager

    if (this.selectedManagerId === null) {

      this.errorMessage =
        'Please select a project manager';

      return;

    }


    this.saving = true;


    // =========================
    // UPDATE PROJECT
    // =========================

    if (
      this.editing &&
      this.editingId !== null
    ) {

      this.projectService
        .updateProject(
          this.editingId,
          this.project
        )
        .subscribe({

          next: (updatedProject: Project) => {

            // After updating project,
            // assign manager.

            this.assignManager(
              this.editingId!,
              this.selectedManagerId!
            );

          },

          error: (error: HttpErrorResponse) => {

            console.error(
              'PROJECT UPDATE ERROR:',
              error
            );

            this.errorMessage =
              error.error?.message ||
              'Failed to update project';

            this.saving = false;

          }

        });

      return;
    }


    // =========================
    // CREATE PROJECT
    // =========================

    this.projectService
      .createProject(this.project)
      .subscribe({

        next: (createdProject: Project) => {

          if (!createdProject.id) {

            this.errorMessage =
              'Project created but ID was not returned';

            this.saving = false;

            return;

          }


          // Assign selected manager

          this.assignManager(
            createdProject.id,
            this.selectedManagerId!
          );

        },

        error: (error: HttpErrorResponse) => {

          console.error(
            'PROJECT CREATE ERROR:',
            error
          );

          this.errorMessage =
            error.error?.message ||
            'Failed to create project';

          this.saving = false;

        }

      });
  }


  // =========================
  // ASSIGN PROJECT MANAGER
  // =========================

  assignManager(
    projectId: number,
    userId: number
  ): void {

    this.projectService
      .assignManager(
        projectId,
        userId
      )
      .subscribe({

        next: (project: Project) => {

          this.successMessage =
            'Project saved and manager assigned successfully';

          this.saving = false;

          this.resetForm();

          this.loadProjects();

        },

        error: (error: HttpErrorResponse) => {

          console.error(
            'MANAGER ASSIGNMENT ERROR:',
            error
          );

          this.errorMessage =
            'Project saved, but manager assignment failed';

          this.saving = false;

          this.loadProjects();

        }

      });
  }


  // =========================
  // EDIT PROJECT
  // =========================

  editProject(project: Project): void {

    this.editing = true;

    this.editingId =
      project.id ?? null;

    this.successMessage = '';

    this.errorMessage = '';


    // Copy project information
    // into the form.

    this.project = {

      name: project.name,

      description: project.description,

      status: project.status

    };


    // Select existing manager

    this.selectedManagerId =
      project.manager?.id ?? null;

  }


  // =========================
  // DELETE PROJECT
  // =========================

  deleteProject(id: number): void {

    const confirmed = confirm(
      'Are you sure you want to delete this project?'
    );


    if (!confirmed) {

      return;

    }


    this.errorMessage = '';

    this.successMessage = '';


    this.projectService
      .deleteProject(id)
      .subscribe({

        next: () => {

          this.successMessage =
            'Project deleted successfully';

          this.loadProjects();

        },

        error: (error: HttpErrorResponse) => {

          console.error(
            'PROJECT DELETE ERROR:',
            error
          );

          this.errorMessage =
            error.error?.message ||
            'Failed to delete project';

        }

      });
  }


  // =========================
  // RESET FORM
  // =========================

  resetForm(): void {

    this.project = {

      name: '',

      description: '',

      status: 'ACTIVE'

    };


    this.selectedManagerId = null;

    this.editing = false;

    this.editingId = null;

    this.saving = false;

  }

}