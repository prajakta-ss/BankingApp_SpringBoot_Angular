import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../services/account.service'; 

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent {
  recipientUsername: string = '';
  amount: number = 0;
  message: string = '';
  error: string = '';
  transactionHistory: { recipient: string, amount: number, status: string, date: Date }[] = [];

  constructor(private accountService: AccountService) {}

  transfer() {
    // Clear previous messages
    this.message = '';
    this.error = '';
  
    // Validate input
    if (!this.recipientUsername?.trim() || this.amount <= 0) {
      this.error = 'Please provide a valid recipient and amount.';
      return;
    }
  
    this.accountService.transfer(this.recipientUsername, this.amount).subscribe({
      next: () => {
        this.message = `Transferred ₹${this.amount} to ${this.recipientUsername}`;
        this.transactionHistory.push({
          recipient: this.recipientUsername,
          amount: this.amount,
          status: 'Success',
          date: new Date()
        });
  
        // Reset form values
        this.amount = 0;
        this.recipientUsername = '';
      },
      error: (err) => {
        // Fallback if backend doesn't send expected structure
        const backendMessage = err?.error?.message || 'Insufficient funds';
        this.error = 'Transfer failed: ' + backendMessage;
      }
    });
  }
  
}
