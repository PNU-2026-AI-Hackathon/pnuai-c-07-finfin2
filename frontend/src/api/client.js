import axios from "axios";

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "https://test-fin.duckdns.org";

const client = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

export function withAuth(accessToken) {
  return accessToken
    ? { headers: { Authorization: `Bearer ${accessToken}` } }
    : {};
}

// AuthContext가 accessToken state를 갱신할 수 있도록 setter를 등록해둔다.
// client.js는 axios 인스턴스만 들고 있고 accessToken 자체는 AuthContext가 소유하기 때문에,
// 인터셉터에서 refresh로 받은 새 토큰을 React state에 반영하려면 이 setter가 필요하다.
let setAccessTokenExternal = null;

export function registerAuthHandlers({ setAccessToken }) {
  setAccessTokenExternal = setAccessToken;
}

let refreshPromise = null;

client.interceptors.response.use(
  (res) => res,
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;
    const isRefreshCall = originalRequest?.url?.includes('/auth/refresh');

    if (status !== 401 || !originalRequest || originalRequest._retry || isRefreshCall) {
      return Promise.reject(error);
    }
    originalRequest._retry = true;

    try {
      if (!refreshPromise) {
        refreshPromise = client.post('/auth/refresh', {}).finally(() => {
          refreshPromise = null;
        });
      }
      const res = await refreshPromise;
      const newAccessToken = res.data.data;

      setAccessTokenExternal?.(newAccessToken);
      originalRequest.headers = {
        ...originalRequest.headers,
        Authorization: `Bearer ${newAccessToken}`,
      };
      return client(originalRequest);
    } catch (refreshError) {
      setAccessTokenExternal?.(null);
      return Promise.reject(error);
    }
  }
);

export default client;
