// Google OAuth Configuration
export const GOOGLE_OAUTH_CONFIG = {
  clientId: "4536067250-cruj6vbtlhhdvrktova725fssla3k5hu.apps.googleusercontent.com",
  clientSecret: "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",
  redirectUri: window.location.origin + "/auth/callback",
  scope: "openid email profile",
  responseType: "code",
  accessType: "offline",
  prompt: "consent"
};

// Google OAuth endpoints
export const GOOGLE_OAUTH_ENDPOINTS = {
  auth: "https://accounts.google.com/o/oauth2/v2/auth",
  token: "https://oauth2.googleapis.com/token",
  userInfo: "https://www.googleapis.com/oauth2/v2/userinfo"
};

// Build Google OAuth URL
export const buildGoogleOAuthUrl = (): string => {
  console.log('Building Google OAuth URL with redirect URI:', GOOGLE_OAUTH_CONFIG.redirectUri);
  console.log('Current origin:', window.location.origin);
  
  const params = new URLSearchParams({
    client_id: GOOGLE_OAUTH_CONFIG.clientId,
    redirect_uri: GOOGLE_OAUTH_CONFIG.redirectUri,
    response_type: GOOGLE_OAUTH_CONFIG.responseType,
    scope: GOOGLE_OAUTH_CONFIG.scope,
    access_type: GOOGLE_OAUTH_CONFIG.accessType,
    prompt: GOOGLE_OAUTH_CONFIG.prompt,
    state: generateRandomState()
  });

  const url = `${GOOGLE_OAUTH_ENDPOINTS.auth}?${params.toString()}`;
  console.log('Generated OAuth URL:', url);
  return url;
};

// Generate random state for CSRF protection
const generateRandomState = (): string => {
  const array = new Uint8Array(32);
  crypto.getRandomValues(array);
  return Array.from(array, byte => byte.toString(16).padStart(2, '0')).join('');
};

// Store state in localStorage for verification
export const storeOAuthState = (state: string): void => {
  localStorage.setItem('oauth_state', state);
};

// Get stored state from localStorage
export const getStoredOAuthState = (): string | null => {
  return localStorage.getItem('oauth_state');
};

// Clear stored state
export const clearOAuthState = (): void => {
  localStorage.removeItem('oauth_state');
}; 