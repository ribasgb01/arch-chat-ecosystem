import { Component, signal, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html'
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = signal('');
  password = signal('');
  username = signal('');
  nickname = signal('');
  birthDate = signal('');
  
  errorMessage = signal('');
  isLoading = signal(false);

  onSubmit() {
    this.isLoading.set(true);
    this.errorMessage.set('');

    const userData = {
      email: this.email(),
      password: this.password(),
      username: this.username(),
      nickname: this.nickname(),
      birthDate: this.birthDate()
    };

    this.authService.register(userData).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.router.navigate(['/verify-email'], { queryParams: { email: this.email() } });
      },
      error: (err) => {
        this.isLoading.set(false);
        const msg = err.error?.error || 'Erro ao criar conta. Verifique os dados.';
        this.errorMessage.set(msg);
      }
    });
  }
}