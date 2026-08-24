// AuthContext.jsx
import { createContext, useContext, useState, useEffect } from "react";
import client, { withAuth } from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(null);
  const [fetchedRole, setFetchedRole] = useState(null);
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    client.post('/auth/refresh', {})
      .then(res => setAccessToken(res.data.data))
      .catch(() => {})
      .finally(() => setIsInitialized(true));
  }, []);

  useEffect(() => {
    if (!accessToken) return; // 토큰이 없으면 조회할 필요가 없음

    let cancelled = false;

    client.get('/user/me', withAuth(accessToken))
      .then(res => { if (!cancelled) setFetchedRole(res.data.userRole); })
      .catch(() => { if (!cancelled) setFetchedRole(null); });

    return () => { cancelled = true; };
  }, [accessToken]);

  const userRole = accessToken ? fetchedRole : null;

  return (
    <AuthContext.Provider value={{ accessToken, setAccessToken, userRole, setUserRole: setFetchedRole, isInitialized }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}