import { useEffect } from "react";
import { useLocation } from "react-router-dom";

// 라우트(경로)가 바뀔 때마다 스크롤을 맨 위로 초기화
export default function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]);

  return null;
}