import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { VerifyEmailComponent } from './features/auth/verify-email/verify-email';
import { ChatComponent } from './features/chat/chat'; // <-- Importe o ChatComponent
import { authGuard } from './core/guards/auth.guard'; // <-- Importe o Guard

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'verify-email', component: VerifyEmailComponent },
  
  { path: 'chat', component: ChatComponent, canActivate: [authGuard] },
  
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];