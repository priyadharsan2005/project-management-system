import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(
  private authService: AuthService,
  private router: Router
) {}

  login() {

    this.errorMessage = '';
    this.loading = true;

    this.authService.login(this.email, this.password).subscribe({
  next: (response: { token: string }) => {
  console.log('Login successful');
  console.log('JWT:', response.token);

  this.authService.saveToken(response.token);

  this.loading = false;

  this.router.navigate(['/dashboard']);
},

      error: (error) => {
        console.error('Login failed:', error);

        this.errorMessage = 'Invalid email or password';
        this.loading = false;
      }

    });
  }
}