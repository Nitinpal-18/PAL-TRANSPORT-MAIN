import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import { useToast } from "@/hooks/use-toast";

const OAuthCallback: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { loginWithGoogle } = useAuth();
  const { toast } = useToast();
  const [isProcessing, setIsProcessing] = useState(true);

  useEffect(() => {
    const handleOAuthCallback = async () => {
      try {
        // Get the authorization code and state from URL parameters
        const code = searchParams.get('code');
        const state = searchParams.get('state');
        const error = searchParams.get('error');

        // Check for OAuth errors
        if (error) {
          console.error('OAuth error:', error);
          toast({
            title: "Authentication failed",
            description: "Google authentication was cancelled or failed. Please try again.",
            variant: "destructive",
          });
          navigate('/login');
          return;
        }

        // Validate required parameters
        if (!code || !state) {
          console.error('Missing OAuth parameters');
          toast({
            title: "Authentication failed",
            description: "Invalid authentication response. Please try again.",
            variant: "destructive",
          });
          navigate('/login');
          return;
        }

        // Process the OAuth callback
        await loginWithGoogle(code, state);
        
        // Redirect to dashboard on success
        navigate('/dashboard');
        
      } catch (error) {
        console.error('OAuth callback error:', error);
        toast({
          title: "Authentication failed",
          description: error instanceof Error ? error.message : "Failed to complete authentication",
          variant: "destructive",
        });
        navigate('/login');
      } finally {
        setIsProcessing(false);
      }
    };

    handleOAuthCallback();
  }, [searchParams, loginWithGoogle, navigate, toast]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-secondary/30">
      <div className="text-center space-y-4">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
        <h2 className="text-xl font-semibold">Completing authentication...</h2>
        <p className="text-muted-foreground">
          {isProcessing ? "Please wait while we complete your sign-in." : "Redirecting..."}
        </p>
      </div>
    </div>
  );
};

export default OAuthCallback; 