import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { AuthService } from '../services/auth.service';
import { DashboardService, DashboardData } from '../services/dashboard.service';
import { TaskService, Task } from '../services/task.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  role: string | null = null;
  userName: string | null = null;

  // Manager dashboard
  totalProjects = 0;
  totalTasks = 0;
  totalEmployees = 0;

  // Task counts
  todoTasks = 0;
  inProgressTasks = 0;
  completedTasks = 0;

  // User dashboard
  myTasks: Task[] = [];
  loadingTasks = false;
  updatingTaskId: number | null = null;
  userError = '';

  constructor(
    private authService: AuthService,
    private dashboardService: DashboardService,
    private taskService: TaskService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {

    this.role = this.authService.getRole();
    this.userName = this.authService.getUserName();

    console.log('Dashboard role:', this.role);

    if (this.role === 'MANAGER') {
      this.loadManagerDashboard();
    } else {
      this.loadUserDashboard();
    }
  }

  // =====================================================
  // MANAGER DASHBOARD
  // =====================================================

  loadManagerDashboard(): void {

    this.dashboardService.getDashboard().subscribe({

      next: (data: DashboardData) => {

        this.totalProjects = data.totalProjects;
        this.totalTasks = data.totalTasks;
        this.totalEmployees = data.totalEmployees;

        this.todoTasks = data.todoTasks;
        this.inProgressTasks = data.inProgressTasks;
        this.completedTasks = data.completedTasks;

        this.cdr.detectChanges();
      },

      error: (error) => {
        console.error('MANAGER DASHBOARD ERROR:', error);
      }
    });
  }

  // =====================================================
  // USER DASHBOARD
  // =====================================================

  loadUserDashboard(): void {

    this.loadingTasks = true;
    this.userError = '';

    // Existing API automatically returns
    // only tasks assigned to the logged-in user.
    this.taskService.getTasks(
      undefined,
      undefined,
      0,
      100,
      'id',
      'asc'
    ).subscribe({

      next: (data) => {

        this.myTasks = data.content;

        this.calculateTaskCounts();

        this.loadingTasks = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('USER TASK ERROR:', error);

        this.userError = 'Failed to load your tasks';

        this.loadingTasks = false;
      }
    });
  }

  // =====================================================
  // CALCULATE USER TASK COUNTS
  // =====================================================

  calculateTaskCounts(): void {

    this.totalTasks = this.myTasks.length;

    this.todoTasks = this.myTasks.filter(
      task => task.status === 'TODO'
    ).length;

    this.inProgressTasks = this.myTasks.filter(
      task => task.status === 'IN_PROGRESS'
    ).length;

    this.completedTasks = this.myTasks.filter(
      task => task.status === 'COMPLETED'
    ).length;
  }

  // =====================================================
  // UPDATE USER TASK STATUS
  // =====================================================

  updateTaskStatus(task: Task, newStatus: string): void {

    if (!task.id) {
      return;
    }

    this.updatingTaskId = task.id;

    const updatedTask: Task = {
      title: task.title,
      description: task.description,
      status: newStatus,
      dueDate: task.dueDate
    };

    this.taskService.updateTask(
      task.id,
      updatedTask
    ).subscribe({

      next: (updated) => {

        task.status = updated.status;

        this.calculateTaskCounts();

        this.updatingTaskId = null;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('TASK UPDATE ERROR:', error);

        this.userError =
          error.error?.message ||
          'Failed to update task status';

        this.updatingTaskId = null;

        this.cdr.detectChanges();
      }
    });
  }

  // =====================================================
  // NAVIGATION
  // =====================================================

  goToProjects(): void {
    this.router.navigate(['/projects']);
  }

  goToTasks(): void {
    this.router.navigate(['/tasks']);
  }

  goToUsers(): void {
    this.router.navigate(['/users']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}