import { Routes } from '@angular/router';

import { Login } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { Projects } from './projects/projects';
import { TaskComponent } from './tasks/task';
import { Users } from './users/users';

export const routes: Routes = [

  {
    path: '',
    component: Login
  },

  {
    path: 'dashboard',
    component: Dashboard
  },

  {
    path: 'projects',
    component: Projects
  },

  {
    path: 'tasks',
    component: TaskComponent
  },

  {
    path: 'users',
    component: Users
  }

];