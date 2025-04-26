import { Component } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-withdraw',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './withdraw.component.html',
  styleUrls: ['./withdraw.component.css']
})
export class WithdrawComponent {
  amount: number = 0;
  message: string = '';
  error: string = '';

  constructor(private accountService: AccountService) {}

  withdraw() {
    this.message = '';
    this.error = '';
  
    
    this.accountService.withdraw(this.amount).subscribe({
      next: () => {
        this.message = 'Withdrawal successful!';
      },
      error: (err) => {
        // Fallback if backend doesn't send expected structure
        const backendMessage = err?.error?.message || 'Insufficient funds';
        this.error = 'Transfer failed: ' + backendMessage;
      }
    });
  }
  
}
