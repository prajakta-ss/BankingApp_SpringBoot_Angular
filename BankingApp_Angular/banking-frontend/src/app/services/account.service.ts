import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { Account } from '../models/account';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private apiUrl = 'http://localhost:8080/api/accounts';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token || ''}`,
      'Content-Type': 'application/json'
    });
  }

  register(account: Account): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/register`, account);
  }

  login(account: { username: string; password: string }): Observable<void> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, account)
      .pipe(
        tap(response => {
          const user = { username: account.username, token: response.token };
          localStorage.setItem('user', JSON.stringify(user));
          this.authService.setCurrentUser(user);
        }),
        map(() => {})
      );
  }

  deposit(amount: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/deposit`, { amount }, {
      headers: this.getAuthHeaders(),
      responseType: 'text'
    });
  }

  withdraw(amount: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/withdraw`, { amount }, {
      headers: this.getAuthHeaders(),
      responseType: 'text'
    });
  }

  transfer(to: string, amount: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/transfer`, { to, amount }, {
      headers: this.getAuthHeaders(),
      responseType: 'text'
    });
  }

  getBalance(): Observable<number> {
    const token = this.authService.getToken();
    if (!token) {
      return new Observable((observer) => {
        observer.error(new Error('User not logged in. Token is missing.'));
      });
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.get<number>(`${this.apiUrl}/balance`, { headers });
  }

  getTransactionHistory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/transactions/history`, {
      headers: this.getAuthHeaders()
    });
  }
}
