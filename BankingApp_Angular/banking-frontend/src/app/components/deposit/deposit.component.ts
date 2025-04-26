import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-deposit',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './deposit.component.html',
  styleUrls: ['./deposit.component.css']
})
export class DepositComponent {
  amount: number = 0;
  message: string = '';
  error: string = '';

  constructor(private accountService: AccountService, private router: Router) {}

  onDeposit() {
    if (this.amount <= 0) {
      this.error = 'Please enter a valid amount.';
      this.message = '';
      return;
    }

    this.accountService.deposit(this.amount).subscribe({
      next: (response: string) => {
        this.message = response; // e.g., "Deposit successful"
        this.error = '';
      },
      error: (err) => {
        this.error = 'Deposit failed. Please try again.';
        this.message = '';
        console.error(err);
      }
    });
  }
}
