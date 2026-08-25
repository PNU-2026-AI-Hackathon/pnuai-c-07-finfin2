// AuthContext.jsx
import { createContext, useContext, useState, useEffect } from "react";
import client, { withAuth } from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(null);
  const [fetchedRole, setFetchedRole] = useState(null);
  const [isInitialized, setIsInitialized] = useState(false);
  // fetchedRole이 어떤 accessToken 기준으로 확정됐는지 기록한다. isRoleLoading을 effect 안에서
  // setState로 관리하면, accessToken이 막 바뀐 바로 그 렌더에서는 아직 반영 전이라 한 틱 새어나간다.
  // 렌더 중에 값을 직접 비교해서 계산하면 그 틈이 생기지 않는다.
  const [resolvedToken, setResolvedToken] = useState(null);

  useEffect(() => {
    client.post('/auth/refresh', {})
      .then(res => setAccessToken(res.data.data))
      .catch(() => {})
      .finally(() => setIsInitialized(true));
  }, []);

  useEffect(() => {
    let cancelled = false;

    (async () => {
      if (!accessToken) {
        if (!cancelled) {
          setFetchedRole(null);
          setResolvedToken(null);
        }
        return;
      }

      try {
        const res = await client.get('/user/me', withAuth(accessToken));
        if (!cancelled) setFetchedRole(res.data.userRole);
      } catch {
        if (!cancelled) setFetchedRole(null);
      } finally {
        if (!cancelled) setResolvedToken(accessToken);
      }
    })();

    return () => { cancelled = true; };
  }, [accessToken]);

  const userRole = accessToken ? fetchedRole : null;
  const isRoleLoading = Boolean(accessToken) && resolvedToken !== accessToken;

  return (
    <AuthContext.Provider value={{ accessToken, setAccessToken, userRole, setUserRole: setFetchedRole, isInitialized, isRoleLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}