import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./login/login.component'),
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register.component'),
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./dashboard/dashboard.component'),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'generate',
        pathMatch: 'full'
      },
      {
        path: 'generate',
        loadComponent: () => import('./dashboard/pages/workout-generator/workout-generator.component')
      },
      {
        path: 'chat',
        loadComponent: () => import('./dashboard/pages/agentic-chat/agentic-chat.component')
      },
      {
        path: 'history',
        loadComponent: () => import('./dashboard/pages/workout-history/workout-history.component')
      },
      {
        path: 'register',
        loadComponent: () => import('./dashboard/pages/workout-register/workout-register.component')
      }
    ]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
];
