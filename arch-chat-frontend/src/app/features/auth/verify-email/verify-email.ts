import { Component, signal, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './verify-email.html'
})
export class VerifyEmailComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute); 

  email = signal('');
  code = signal('');
  errorMessage = signal('');
  isLoading = signal(false);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['email']) {
        this.email.set(params['email']);
      }
    });
  }

  onSubmit() {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.authService.verifyEmail(this.email(), this.code()).subscribe({
      next: () => {
        alert('Conta verificada com sucesso! Você já pode fazer login.');
        this.router.navigate(['/login']); 
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.error || 'Código inválido ou expirado.');
      }
    });
  }
}