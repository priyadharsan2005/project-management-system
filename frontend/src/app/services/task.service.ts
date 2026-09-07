import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Task {
  id?: number;
  title: string;
  description: string;
  status: string;
  dueDate: string;
  project?: {
    id: number;
    name: string;
    description?: string;
    status?: string;
  };
  assignedUser?: {
    id: number;
    name: string;
    email: string;
    role: string;
  };
}

export interface TaskPage {
  content: Task[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  // GET /api/tasks
  getTasks(
    status?: string,
    title?: string,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    direction: string = 'asc'
  ): Observable<TaskPage> {

    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);

    if (status) {
      params = params.set('status', status);
    }

    if (title) {
      params = params.set('title', title);
    }

    return this.http.get<TaskPage>(
      this.apiUrl,
      { params }
    );
  }

  // GET /api/tasks/{id}
  getTask(id: number): Observable<Task> {
    return this.http.get<Task>(
      `${this.apiUrl}/${id}`
    );
  }

  // POST /api/tasks
  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(
      this.apiUrl,
      task
    );
  }

  // PUT /api/tasks/{id}
  updateTask(id: number, task: Task): Observable<Task> {
    return this.http.put<Task>(
      `${this.apiUrl}/${id}`,
      task
    );
  }

  // DELETE /api/tasks/{id}
  deleteTask(id: number): Observable<string> {
    return this.http.delete(
      `${this.apiUrl}/${id}`,
      { responseType: 'text' }
    );
  }

  // PUT /api/tasks/{taskId}/project/{projectId}
  assignProject(
    taskId: number,
    projectId: number
  ): Observable<Task> {

    return this.http.put<Task>(
      `${this.apiUrl}/${taskId}/project/${projectId}`,
      {}
    );
  }

  // PUT /api/tasks/{taskId}/user/{userId}
  assignUser(
    taskId: number,
    userId: number
  ): Observable<Task> {

    return this.http.put<Task>(
      `${this.apiUrl}/${taskId}/user/${userId}`,
      {}
    );
  }
}