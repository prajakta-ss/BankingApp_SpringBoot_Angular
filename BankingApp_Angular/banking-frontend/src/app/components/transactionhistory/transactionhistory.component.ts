import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './transactionhistory.component.html',
  styleUrls: ['./transactionhistory.component.css']
})
export class TransactionHistoryComponent implements OnInit {
  transactions: any[] = [];
  message: string = '';

  constructor(private accountService: AccountService) {}

  ngOnInit() {
    this.accountService.getTransactionHistory().subscribe({
      next: (data: any[]) => {
        this.transactions = data;
      },
      error: (err) => {
        this.message = 'Failed to load transaction history: ' + err.error.message;
      }
    });
  }
}
