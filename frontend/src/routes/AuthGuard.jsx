import { useEffect } from 'react';
import { useAuth } from "../context/AuthContext";
import { useNavigate } from 'react-router-dom';

export default function AuthGuard({ children }) {
  const navigate = useNavigate();
  const { accessToken, userRole, isInitialized } = useAuth();

  useEffect(() => {
    if (!isInitialized) return; // refresh 완료 대기
    if (!accessToken) {
      navigate('/login');
      return;
    }
    if (userRole === 'BEFORE_AGREED') {
      navigate('/terms');
    }
  }, [isInitialized, accessToken, userRole, navigate]);

  const isReady = isInitialized && Boolean(accessToken) && userRole !== null && userRole !== 'BEFORE_AGREED';

  if (!isReady) return <div className="min-h-screen bg-teal-50/40" />;

  // 검사 통과하면 감싸고 있던 원래 children를 그대로 보여줌
  return <>{children}</>;
}