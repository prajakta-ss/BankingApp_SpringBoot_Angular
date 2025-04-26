import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { BehaviorSubject } from "rxjs/internal/BehaviorSubject";
import { Observable } from "rxjs/internal/Observable";
import { Account } from "../models/account";
import { tap } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/accounts';
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;

  constructor(private http: HttpClient, private router: Router) {

    this.currentUserSubject = new BehaviorSubject<any>(JSON.parse(localStorage.getItem('user')!));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }
  setCurrentUser(user: any): void {
    this.currentUserSubject.next(user);
  }

  isLoggedIn(username: string, password: string): Observable<any> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { username, password })
      .pipe(
        tap(response => {
          const user = { username, token: response.token };
          localStorage.setItem('user', JSON.stringify(user));
          this.currentUserSubject.next(user);
        })
      );
  }

  register(account: Account): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, account);
  }

  logout() {
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/']);
  }

  isAuthenticated() {
    return this.currentUserValue !== null;
  }

  public getUsername(): string {
    const user = this.currentUserValue;
    return user ? user.username : ''; // Assuming user object contains a username property
  }

  public getToken(): string | null {
    const user = this.currentUserValue;
    const token = user?.token;
    console.log("Retrieved Token:", token);  // Check if token is present
    return token || null;
  }
  

  // Helper method to decode JWT
  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];  // JWT token format is header.payload.signature
      const decoded = atob(payload); // Decode base64
      return JSON.parse(decoded);
    } catch (e) {
      return null;
    }
  }

  // Method to check if token is expired
  public isTokenExpired(token: string): boolean {
    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) {
      return true; // If no expiration found, consider it expired
    }
    const exp = decoded.exp * 1000; // JWT exp is in seconds, convert to milliseconds
    return exp < Date.now();
  }
}
