import { Component, inject } from '@angular/core';
import { AuthService } from '../core/auth/auth.service';

import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-dashboard',
    imports: [RouterModule],
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export default class DashboardComponent {
  authService = inject(AuthService);
  user = this.authService.currentUser;

  logout() {
    this.authService.logout();
  }
}
