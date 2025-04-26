import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule,FormsModule], // No additional modules needed for this component
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  username: string = '';
  balance: number = 0;
  isAuthenticated: boolean = false; // Add this line

  constructor(
    private accountService: AccountService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.isAuthenticated = this.authService.isAuthenticated(); // Set the flag
    if (this.isAuthenticated) {
      this.loadUserInfo();
      this.loadBalance();
    } else {
      this.router.navigate(['/login']);
    }
  }

  loadUserInfo() {
    this.username = this.authService.getUsername();
  }

  loadBalance() {
    this.accountService.getBalance().subscribe({
      next: (balance: number) => {
        this.balance = balance;
      },
      error: (err) => {
        console.error('Error fetching balance', err);
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
