import React from "react";
import { Button } from "@/components/ui/button";
import { buildGoogleOAuthUrl, storeOAuthState } from "@/config/googleOAuth";

interface GoogleOAuthButtonProps {
  isLoading?: boolean;
  disabled?: boolean;
  className?: string;
  children?: React.ReactNode;
}

const GoogleOAuthButton: React.FC<GoogleOAuthButtonProps> = ({
  isLoading = false,
  disabled = false,
  className = "",
  children
}) => {
  const handleGoogleLogin = () => {
    try {
      // Build the OAuth URL
      const oauthUrl = buildGoogleOAuthUrl();
      
      // Extract and store the state parameter
      const urlParams = new URLSearchParams(oauthUrl.split('?')[1]);
      const state = urlParams.get('state');
      if (state) {
        storeOAuthState(state);
      }
      
      // Redirect to Google OAuth
      window.location.href = oauthUrl;
    } catch (error) {
      console.error('Error initiating Google OAuth:', error);
    }
  };

  return (
    <Button
      type="button"
      variant="outline"
      onClick={handleGoogleLogin}
      disabled={isLoading || disabled}
      className={`w-full h-11 font-medium transition-all hover:shadow-md ${className}`}
    >
      {isLoading ? (
        <div className="flex items-center justify-center">
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-current mr-2"></div>
          Connecting to Google...
        </div>
      ) : (
        <div className="flex items-center justify-center">
          <svg className="w-5 h-5 mr-2" viewBox="0 0 24 24">
            <path
              fill="#4285F4"
              d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
            />
            <path
              fill="#34A853"
              d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
            />
            <path
              fill="#FBBC05"
              d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
            />
            <path
              fill="#EA4335"
              d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
            />
          </svg>
          {children || "Continue with Google"}
        </div>
      )}
    </Button>
  );
};

export default GoogleOAuthButton; 