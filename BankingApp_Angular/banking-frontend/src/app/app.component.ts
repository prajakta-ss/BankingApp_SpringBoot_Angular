import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { AccountService } from './services/account.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Welcome to Online Banking';
  balance: number = 0;
  username: string = '';
  isAuthenticated: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private accountService: AccountService
  ) {}

  ngOnInit(): void {
    // Subscribe to authentication status
    this.authService.currentUser.subscribe(user => {
      this.isAuthenticated = !!user;
      if (user) {
        this.username = user.username;
        this.loadBalance();
      }
    });
  }

  loadBalance(): void {
    this.accountService.getBalance().subscribe({
      next: (balance: number) => {
        this.balance = balance;
      },
      error: (err) => {
        console.error('Error fetching balance', err);
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
