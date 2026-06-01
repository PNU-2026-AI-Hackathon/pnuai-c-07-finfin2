// AuthContext.jsx
import { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(null);
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    axios.post('https://test-fin.duckdns.org/auth/refresh', {}, { withCredentials: true })
      .then(res => setAccessToken(res.data.data))
      .catch(() => {})
      .finally(() => setIsInitialized(true));
  }, []);

  return (
    <AuthContext.Provider value={{ accessToken, setAccessToken, isInitialized }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}