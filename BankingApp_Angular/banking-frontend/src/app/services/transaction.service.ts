import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Transaction {
  id?: number;
  amount: number;
  type: string;
  timestamp: string;
  accountId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private baseUrl = '/api/accounts';
  private apiUrl = '/api/bank/transactions/history'; 

  constructor(private http: HttpClient) {}

  deposit(amount: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/deposit`, { amount });
  }

  withdraw(amount: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/withdraw`, { amount });
  }

  transfer(toUsername: string, amount: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/transfer`, { toUsername, amount });
  }

  getTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/transactions`);
  }
  getTransactionHistory(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.apiUrl);
  }
}
