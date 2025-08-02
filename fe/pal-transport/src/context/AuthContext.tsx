
import React, { createContext, useContext, useEffect, useState } from "react";
import { User } from "../models/user";
import { getCurrentUser, isAuthenticated, login, logout, setAuthData, authenticateWithGoogle } from "../services/authService";
import { LoginCredentials } from "../models/user";
import { useToast } from "@/components/ui/use-toast";

interface AuthContextType {
  user: User | null;
  isLoggedIn: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  loginWithGoogle: (code: string, state: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  useEffect(() => {
    const initAuth = () => {
      const currentUser = getCurrentUser();
      const isLoggedIn = isAuthenticated();
      
      if (isLoggedIn && currentUser) {
        setUser(currentUser);
      }
      
      setLoading(false);
    };
    
    initAuth();
  }, []);

  const handleLogin = async (credentials: LoginCredentials) => {
    try {
      setLoading(true);
      const response = await login(credentials);
      setAuthData(response);
      setUser(response.user);
      toast({
        title: "Login successful",
        description: `Welcome back, ${response.user.name}!`,
      });
    } catch (error) {
      console.error("Login error:", error);
      toast({
        title: "Login failed",
        description: error instanceof Error ? error.message : "Invalid credentials",
        variant: "destructive",
      });
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = async (code: string, state: string) => {
    try {
      setLoading(true);
      const response = await authenticateWithGoogle(code, state);
      setAuthData(response);
      setUser(response.user);
      toast({
        title: "Google login successful",
        description: `Welcome, ${response.user.name}!`,
      });
    } catch (error) {
      console.error("Google login error:", error);
      toast({
        title: "Google login failed",
        description: error instanceof Error ? error.message : "Google authentication failed",
        variant: "destructive",
      });
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    setUser(null);
  };

  const value = {
    user,
    isLoggedIn: !!user,
    login: handleLogin,
    loginWithGoogle: handleGoogleLogin,
    logout: handleLogout,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
