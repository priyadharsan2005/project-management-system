import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { UserService, User } from '../services/user.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class Users implements OnInit {

  users: User[] = [];

  user: User = {
    name: '',
    email: '',
    role: 'EMPLOYEE',
    password: ''
  };

  editing = false;
  editingId: number | null = null;

  loading = false;
  saving = false;

  errorMessage = '';
  successMessage = '';

  constructor(
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {

    this.loading = true;
    this.errorMessage = '';

    this.userService.getUsers().subscribe({

      next: (data: User[]) => {

        this.users = data;
        this.loading = false;

      },

      error: (error: HttpErrorResponse) => {

        console.error('USER LOAD ERROR:', error);

        this.errorMessage = 'Failed to load employees';
        this.loading = false;

      }

    });
  }

  saveUser(): void {

    this.errorMessage = '';
    this.successMessage = '';

    if (!this.user.name?.trim()) {

      this.errorMessage = 'Name is required';
      return;

    }

    if (!this.user.email?.trim()) {

      this.errorMessage = 'Email is required';
      return;

    }

    if (!this.user.role) {

      this.errorMessage = 'Role is required';
      return;

    }

    if (
      !this.editing &&
      !this.user.password?.trim()
    ) {

      this.errorMessage = 'Password is required';
      return;

    }

    this.saving = true;

    // UPDATE
    if (
      this.editing &&
      this.editingId !== null
    ) {

      this.userService
        .updateUser(
          this.editingId,
          this.user
        )
        .subscribe({

          next: (updatedUser: User) => {

            this.successMessage =
              'Employee updated successfully';

            this.saving = false;

            this.resetForm();

            this.loadUsers();

          },

          error: (error: HttpErrorResponse) => {

            console.error(
              'USER UPDATE ERROR:',
              error
            );

            this.errorMessage =
              error.error?.message ||
              'Failed to update employee';

            this.saving = false;

          }

        });

      return;
    }

    // CREATE
    this.userService
      .createUser(this.user)
      .subscribe({

        next: (createdUser: User) => {

          this.successMessage =
            'Employee created successfully';

          this.saving = false;

          this.resetForm();

          this.loadUsers();

        },

        error: (error: HttpErrorResponse) => {

          console.error(
            'USER CREATE ERROR:',
            error
          );

          this.errorMessage =
            error.error?.message ||
            'Failed to create employee';

          this.saving = false;

        }

      });
  }

  editUser(user: User): void {

    this.editing = true;

    this.editingId =
      user.id ?? null;

    this.successMessage = '';
    this.errorMessage = '';

    this.user = {

      id: user.id,

      name: user.name,

      email: user.email,

      role: user.role,

      password: ''

    };
  }

  deleteUser(id: number): void {

    if (
      !confirm(
        'Are you sure you want to delete this employee?'
      )
    ) {

      return;

    }

    this.errorMessage = '';
    this.successMessage = '';

    this.userService
      .deleteUser(id)
      .subscribe({

        next: () => {

          this.successMessage =
            'Employee deleted successfully';

          this.loadUsers();

        },

        error: (error: HttpErrorResponse) => {

          console.error(
            'USER DELETE ERROR:',
            error
          );

          this.errorMessage =
            error.error?.message ||
            'Failed to delete employee';

        }

      });
  }

  resetForm(): void {

    this.user = {

      name: '',
      email: '',
      role: 'EMPLOYEE',
      password: ''

    };

    this.editing = false;

    this.editingId = null;

    this.saving = false;
  }
}