
import { LoginCredentials, AuthResponse, User } from "../models/user";
import { getStoredOAuthState, clearOAuthState } from "../config/googleOAuth";
import { apiClient, authApiClient } from "./serviceUtils";

// Google OAuth authentication
export const authenticateWithGoogle = async (code: string, state: string): Promise<AuthResponse> => {
  try {
    // Verify state parameter for CSRF protection
    const storedState = getStoredOAuthState();
    if (!storedState || storedState !== state) {
      throw new Error("Invalid state parameter");
    }

    // Send authorization code to backend for processing
    const backendResponse = await authApiClient.post<any>('/auth/google', {
      code,
      state
    });
    
    // Clear the stored state immediately after successful token exchange
    clearOAuthState();
    
    // Transform backend response to frontend format
    const response: AuthResponse = {
      user: {
        id: backendResponse.user.id.toString(),
        email: backendResponse.user.email,
        name: backendResponse.user.name,
        role: backendResponse.user.role as 'ADMIN' | 'STAFF' | 'USER',
        picture: backendResponse.user.pictureUrl,
        provider: backendResponse.user.provider.toLowerCase() as 'email' | 'google'
      },
      token: backendResponse.token,
      refreshToken: backendResponse.refreshToken
    };
    
    return response;
  } catch (error: any) {
    let message = 'Google authentication failed';
    if (error?.response?.data?.message) {
      message = error.response.data.message;
    } else if (error?.message) {
      message = error.message;
    }
    console.error('Google OAuth error:', error);
    throw new Error(message);
  }
};

export const login = async (credentials: LoginCredentials): Promise<AuthResponse> => {
  try {
    // Make actual API call to backend authentication endpoint
    const backendResponse = await authApiClient.post<any>('/auth/login', credentials);
    
    // Transform backend response to frontend format
    const response: AuthResponse = {
      user: {
        id: backendResponse.user.id.toString(),
        email: backendResponse.user.email,
        name: backendResponse.user.name,
        role: backendResponse.user.role as 'ADMIN' | 'STAFF' | 'USER',
        picture: backendResponse.user.pictureUrl,
        provider: backendResponse.user.provider.toLowerCase() as 'email' | 'google'
      },
      token: backendResponse.token,
      refreshToken: backendResponse.refreshToken
    };
    
    return response;
  } catch (error: any) {
    let message = 'Login failed';
    if (error?.response?.data?.message) {
      message = error.response.data.message;
    } else if (error?.message) {
      message = error.message;
    }
    console.error('Login error:', error);
    throw new Error(message);
  }
};

export const getCurrentUser = (): User | null => {
  try {
    const token = localStorage.getItem('token');
    if (!token) return null;
    
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;
    
    return JSON.parse(userStr);
  } catch (error) {
    console.error('Error getting current user:', error);
    return null;
  }
};

export const isAuthenticated = (): boolean => {
  const token = localStorage.getItem('token');
  return !!token;
};

export const logout = (): void => {
  localStorage.removeItem('token');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('user');
  localStorage.removeItem('oauthState');
};

export const setAuthData = (authResponse: AuthResponse): void => {
  localStorage.setItem('token', authResponse.token);
  localStorage.setItem('refreshToken', authResponse.refreshToken);
  localStorage.setItem('user', JSON.stringify(authResponse.user));
};
