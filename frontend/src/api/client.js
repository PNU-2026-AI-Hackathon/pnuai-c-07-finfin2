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

export default client;
