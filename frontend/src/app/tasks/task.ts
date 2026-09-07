import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import {
  TaskService,
  Task
} from '../services/task.service';
import {
  ProjectService,
  Project
} from '../services/project.service';

import {
  UserService,
  User
} from '../services/user.service';

@Component({
  selector: 'app-task',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task.html',
  styleUrl: './task.css'
})
export class TaskComponent implements OnInit {

  tasks: Task[] = [];

  loading = false;
  saving = false;

  errorMessage = '';
  successMessage = '';

  showForm = false;
  editing = false;
  editingId: number | null = null;
  searchTitle = '';
  selectedStatus = '';
  currentPage = 0;
pageSize = 10;

totalElements = 0;
totalPages = 0;

sortBy = 'id';
direction = 'asc';

  task: Task = {
    title: '',
    description: '',
    status: 'TODO',
    dueDate: ''
  };

  constructor(
  private taskService: TaskService,
  private projectService: ProjectService,
  private userService: UserService,
  private cdr: ChangeDetectorRef
)  {}

  ngOnInit(): void {

  console.log('===== TASKS PAGE LOADED =====');

  this.loadTasks();
  this.loadProjects();
  this.loadUsers();

}

 loadTasks(): void {

  this.loading = true;
  this.errorMessage = '';

  this.taskService.getTasks(
    this.selectedStatus,
    this.searchTitle,
    this.currentPage,
    this.pageSize,
    this.sortBy,
    this.direction
  ).subscribe({

    next: (data) => {

      console.log('TASK API RESPONSE:', data);

      this.tasks = data.content;

      this.totalElements = data.totalElements;
      this.totalPages = data.totalPages;

      console.log('TASKS:', this.tasks);
      console.log('TOTAL ELEMENTS:', this.totalElements);
      console.log('TOTAL PAGES:', this.totalPages);

      this.loading = false;

      this.cdr.detectChanges();
    },

    error: (error: HttpErrorResponse) => {

      console.error('TASK API ERROR:', error);

      this.errorMessage = 'Failed to load tasks';

      this.loading = false;

      this.cdr.detectChanges();
    }

  });
}

  openCreateForm(): void {

    this.showForm = true;
    this.editing = false;

    this.successMessage = '';
    this.errorMessage = '';

    this.resetTaskForm();
  }
cancelCreate(): void {

  this.showForm = false;

  this.editing = false;
  this.editingId = null;

  this.successMessage = '';
  this.errorMessage = '';

  this.resetTaskForm();
}

  createTask(): void {

  this.errorMessage = '';
  this.successMessage = '';

  // Validate title
  if (!this.task.title.trim()) {
    this.errorMessage = 'Task title is required';
    return;
  }

  // Validate due date
  if (!this.task.dueDate) {
    this.errorMessage = 'Due date is required';
    return;
  }

  this.saving = true;

  console.log(
    this.editing
      ? 'UPDATING TASK:'
      : 'CREATING TASK:',
    this.task
  );

  // EDIT
  if (this.editing && this.editingId !== null) {

    this.taskService
      .updateTask(this.editingId, this.task)
      .subscribe({

        next: (updatedTask: Task) => {

          console.log('TASK UPDATED:', updatedTask);

          this.successMessage =
            'Task updated successfully';

          this.saving = false;
          this.showForm = false;

          this.resetTaskForm();

          this.editing = false;
          this.editingId = null;

          this.loadTasks();

          this.cdr.detectChanges();
        },

        error: (error: HttpErrorResponse) => {

          console.error(
            'UPDATE TASK ERROR:',
            error
          );

          this.errorMessage =
            error.error?.message ||
            'Failed to update task';

          this.saving = false;

          this.cdr.detectChanges();
        }

      });

    return;
  }

  // CREATE
  this.taskService
    .createTask(this.task)
    .subscribe({

      next: (createdTask: Task) => {

        console.log(
          'TASK CREATED:',
          createdTask
        );

        this.successMessage =
          'Task created successfully';

        this.saving = false;
        this.showForm = false;

        this.resetTaskForm();

        this.loadTasks();

        this.cdr.detectChanges();
      },

      error: (error: HttpErrorResponse) => {

        console.error(
          'CREATE TASK ERROR:',
          error
        );

        this.errorMessage =
          error.error?.message ||
          'Failed to create task';

        this.saving = false;

        this.cdr.detectChanges();
      }

    });
}

  editTask(task: Task): void {

  this.showForm = true;

  this.editing = true;
  this.editingId = task.id ?? null;

  this.successMessage = '';
  this.errorMessage = '';

  this.task = {
    title: task.title,
    description: task.description,
    status: task.status,
    dueDate: task.dueDate
  };

}
deleteTask(id: number): void {

  const confirmed = confirm(
    'Are you sure you want to delete this task?'
  );

  if (!confirmed) {
    return;
  }

  this.errorMessage = '';
  this.successMessage = '';

  console.log('DELETING TASK:', id);

  this.taskService.deleteTask(id).subscribe({

    next: (response) => {

      console.log(
        'TASK DELETED:',
        response
      );

      this.successMessage =
        'Task deleted successfully';

      this.loadTasks();

      this.cdr.detectChanges();
    },

    error: (error: HttpErrorResponse) => {

      console.error(
        'DELETE TASK ERROR:',
        error
      );

      this.errorMessage =
        error.error?.message ||
        'Failed to delete task';

      this.cdr.detectChanges();
    }

  });
}
loadProjects(): void {

  this.projectService.getProjects().subscribe({

    next: (data) => {

      console.log('PROJECTS:', data);

      this.projects = data;

    },

    error: (error: HttpErrorResponse) => {

      console.error(
        'PROJECT API ERROR:',
        error
      );

    }

  });

}

loadUsers(): void {

  this.userService.getUsers().subscribe({

    next: (data) => {

      console.log('USERS:', data);

      this.users = data;

    },

    error: (error: HttpErrorResponse) => {

      console.error(
        'USER API ERROR:',
        error
      );

    }

  });

}
nextPage(): void {

  if (this.currentPage < this.totalPages - 1) {

    this.currentPage++;

    this.loadTasks();

  }

}

previousPage(): void {

  if (this.currentPage > 0) {

    this.currentPage--;

    this.loadTasks();

  }

}
changeSort(column: string): void {

  if (this.sortBy === column) {

    this.direction =
      this.direction === 'asc'
        ? 'desc'
        : 'asc';

  } else {

    this.sortBy = column;
    this.direction = 'asc';

  }

  this.currentPage = 0;

  this.loadTasks();

}

  resetTaskForm(): void {

  this.task = {
    title: '',
    description: '',
    status: 'TODO',
    dueDate: ''
  };
}

  searchTasks(): void {

  this.currentPage = 0;

  this.loadTasks();

}
clearFilters(): void {

  this.searchTitle = '';
  this.selectedStatus = '';

  this.currentPage = 0;

  this.loadTasks();

}
projects: Project[] = [];
users: User[] = [];

selectedProjectId: number | null = null;
selectedUserId: number | null = null;

}