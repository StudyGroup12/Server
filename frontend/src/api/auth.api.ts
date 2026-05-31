import api from './axios';
import { LoginRequest, SignupRequest, TokenResponse, MemberResponse, ApiResponse } from '../types/auth.types';

export const signup = async (data: SignupRequest): Promise<ApiResponse<void>> => {
  const response = await api.post('/api/auth/signup', data);
  return response.data;
};

export const login = async (data: LoginRequest): Promise<ApiResponse<TokenResponse>> => {
  const response = await api.post('/api/auth/login', data);
  return response.data;
};

export const getMyInfo = async (): Promise<ApiResponse<MemberResponse>> => {
  const response = await api.get('/api/auth/me');
  return response.data;
};
