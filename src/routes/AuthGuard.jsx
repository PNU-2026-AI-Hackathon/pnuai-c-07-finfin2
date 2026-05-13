import { useEffect, useState } from 'react';
import { useAuth } from "../context/AuthContext";
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function AuthGuard({ children }) {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);
  const { setAccessToken } = useAuth();

  useEffect(() => {
    const checkAuth = async () => {
      try {
        // 토큰 발급
        const refreshResponse = await axios.post('https://test-fin.duckdns.org/auth/refresh', {}, {
          withCredentials: true
        });
        
        // 백엔드가 준 Access Token 꺼내기
        const accessToken = refreshResponse.data.data;
        setAccessToken(accessToken);

        // 유저 정보 조회
        const response = await axios.get('https://test-fin.duckdns.org/user/me', {
          headers: { Authorization: `Bearer ${accessToken}` }
        });

        console.log(response.data);
        
        // 권한 확인
        // ----------- 잠시 주석처리!!!!!!!!!!!! ---------------
        // if (response.data.userRole === 'BEFORE_AGREED') {
        //   setIsLoading(false);
        //   navigate('/terms');
        // } else {
          setIsLoading(false);
        // }
      } catch (error) {
        console.error("로그인 안 됨:", error);
        navigate('/login'); // 쫓아내기
      }
    };

    checkAuth();
  }, [navigate]);

  if (isLoading) return <div className="min-h-screen" />;

  // 검사 통과하면 감싸고 있던 원래 children를 그대로 보여줌
  return <>{children}</>;
}