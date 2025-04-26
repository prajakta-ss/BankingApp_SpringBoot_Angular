import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule,
    ReactiveFormsModule,
    CommonModule,
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  registerForm: FormGroup;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    public router: Router,
    private snackBar: MatSnackBar
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      age: ['', [Validators.required, Validators.min(1)]],
      address: ['', [Validators.required]],
    });
  }

  get f() { return this.registerForm.controls; }

  onSubmit() {
    if (this.registerForm.valid) {
      const user = { 
        ...this.registerForm.value, 
        roles: ['USER'],
        balance: 0 
      };
      
      this.authService.register(user).subscribe({
        next: () => {
          this.snackBar.open('Registration successful!', 'Close', { duration: 3000 });
          this.router.navigate(['/login']);
        },
        error: (err) => {
          const errorMessage = err.error?.message || err.message || 'Unknown error occurred';
          this.snackBar.open(`Registration failed: ${errorMessage}`, 'Close', { 
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}