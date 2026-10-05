import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';
import { tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  
  private authUrl = '/api/auth';
  private usersUrl = '/api/users';

  login(email: string, password: string) {
    return this.http.post<{ accessToken: string }>(
      `${this.authUrl}/login`,
      { email, password },
      { withCredentials: true }
    ).pipe(
      tap(response => {
        localStorage.setItem('token', response.accessToken);
        console.log('Login efetuado com sucesso!');
      })
    );
  }

  getCurrentUser(): { userId: string; username: string; nickname: string } | null {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      return jwtDecode<{ userId: string; username: string; nickname: string }>(token);
    } catch {
      return null;
    }
  }

  register(userData: any) {
    return this.http.post(`${this.usersUrl}/register`, userData);
  }

  verifyEmail(email: string, code: string) {
    return this.http.post(`${this.usersUrl}/verify-email`, { email, code });
  }

  logout() {
    return this.http.post(`${this.authUrl}/logout`, {}, { withCredentials: true });
  }
}