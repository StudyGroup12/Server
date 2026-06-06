import axios from 'axios';

const api = axios.create({
  baseURL: '/',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // 쿠키(Refresh Token) 전송을 위해 필수
});

// Request Interceptor: 모든 요청에 Access Token 첨부
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: 401 에러 발생 시 토큰 재발급 시도
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // /api/auth/refresh 엔드포인트 호출 (HttpOnly 쿠키의 Refresh Token 사용)
        const response = await axios.post('/api/auth/refresh', {}, { withCredentials: true });
        const { accessToken } = response.data.data;
        
        sessionStorage.setItem('accessToken', accessToken);
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        
        return api(originalRequest);
      } catch (refreshError) {
        // 재발급 실패 시 로그아웃 처리
        sessionStorage.removeItem('accessToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
