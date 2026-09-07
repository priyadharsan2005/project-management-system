import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const token = localStorage.getItem('token');

  console.log('INTERCEPTOR:', req.url);
  console.log('TOKEN EXISTS:', !!token);

  // Don't add JWT to login request
  if (req.url.includes('/api/auth/login')) {
    return next(req);
  }

  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    console.log('JWT ADDED TO:', req.url);

    return next(authReq);
  }

  console.log('NO TOKEN:', req.url);

  return next(req);
};