export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error: {
    code: string;
    message: string;
  } | null;
}

export interface SignupRequest {
  email: string;
  password?: string; // Optional if needed, but usually required
  nickname: string;
}

// Re-defining to match backend DTOs exactly
export interface LoginRequest {
  email: string;
  password?: string;
}

export interface TokenResponse {
  accessToken: string;
}

export interface MemberResponse {
  id: number;
  email: string;
  nickname: string;
}
