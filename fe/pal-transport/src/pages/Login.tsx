
import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import LoginForm from "@/components/LoginForm";

const Login: React.FC = () => {
  const { isLoggedIn, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (isLoggedIn) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="min-h-screen grid grid-cols-1 md:grid-cols-2">
      <div className="hidden md:block bg-primary/5">
        <div className="h-full flex flex-col items-center justify-center p-8 animate-fade-in">
          <div className="max-w-md mx-auto text-center">
            <div className="bg-primary text-primary-foreground font-bold text-2xl w-16 h-16 rounded-xl flex items-center justify-center mx-auto mb-8">
              PT
            </div>
            <h1 className="text-3xl font-bold mb-4">PAL-Transport</h1>
            <p className="text-muted-foreground">
              Streamline your logistics operations with our comprehensive transport management platform. Track vehicles, manage drivers, and optimize routes with ease.
            </p>
          </div>
        </div>
      </div>
      <div className="flex items-center justify-center p-6">
        <div className="w-full max-w-md space-y-8">
          <div className="text-center md:hidden mb-8">
            <div className="bg-primary text-primary-foreground font-bold text-2xl w-14 h-14 rounded-xl flex items-center justify-center mx-auto mb-4">
              PT
            </div>
            <h1 className="text-2xl font-bold">PAL-Transport</h1>
          </div>
          <LoginForm />
        </div>
      </div>
    </div>
  );
};

export default Login;
