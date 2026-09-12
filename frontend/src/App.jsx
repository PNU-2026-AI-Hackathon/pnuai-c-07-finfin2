import { useEffect } from 'react';
import { BrowserRouter, Routes, Route, useLocation, useNavigate } from 'react-router-dom';
import Header from './components/Header'
import Footer from './components/Footer';
import Login from './pages/Login';
import Introduce from './pages/Introduce';
import Agreement from './pages/Agreement';
import AuthGuard from './routes/AuthGuard';
import Recommend from './pages/Recommend';
import ProductList from './pages/ProductList';
import ProductDetail from './pages/ProductDetail';
import ProductRateCalculator from './pages/ProductRateCalculator';
import MyPage from './pages/MyPage';
import ScrollToTop from "./components/ScrollToTop";
import { useAuth } from './context/AuthContext';

function isMockMode() {
  return import.meta.env.DEV
    && new URLSearchParams(window.location.search).get("mock") === "true";
}

function RecommendRoute() {
  // 상품 추천의 적합도순(탭 A)은 비로그인 사용자도 이용할 수 있다.
  // 로그인 및 상세 정보 입력 여부에 따른 금리순(탭 B) 제한은 결과 응답의
  // `tabs.tabBEnabled`를 기준으로 추천 결과 화면에서 처리한다.
  return <Recommend />;
}

function MyPageRoute() {
  if (isMockMode()) return <MyPage />;
  return <AuthGuard><MyPage /></AuthGuard>;
}

// 로그인 여부(accessToken)는 알지만 약관 동의 여부(userRole)를 아직 못 받아온 짧은 구간에는
// 페이지를 그대로 보여주지 않고 비워둔다. 약관 미동의(BEFORE_AGREED) 상태라면 /terms로 보내야
// 하는데, 이 판단과 실제 리다이렉트(navigate)를 같은 렌더에서 처리해야 "요청한 페이지가 한 프레임
// 보였다가 /terms로 튕기는" 깜빡임이 생기지 않는다. navigate 자체는 useEffect로 하되, 그 결과가
// 반영되기 전까지는 children 대신 계속 빈 화면을 보여줘서 깜빡임을 막는다.
function AuthGate({ children }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { accessToken, userRole, isInitialized, isRoleLoading } = useAuth();
  const isDeterminingRole = !isInitialized || (Boolean(accessToken) && isRoleLoading);
  const needsTermsRedirect = !isDeterminingRole
    && Boolean(accessToken)
    && userRole === 'BEFORE_AGREED'
    && location.pathname !== '/terms';

  useEffect(() => {
    if (needsTermsRedirect) {
      navigate('/terms', { replace: true });
    }
  }, [needsTermsRedirect, navigate]);

  if (isDeterminingRole || needsTermsRedirect) {
    return <div className="min-h-screen bg-white" />;
  }

  return children;
}

function App() {
  return (
    <BrowserRouter>
      <ScrollToTop />
      <Header />
      <AuthGate>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/introduce" element={<Introduce />} />
          <Route path="/recommend" element={<RecommendRoute />} />
          <Route path="/products" element={<ProductList/>}/>
          <Route path="/products/:productId" element={<ProductDetail />} />
          <Route path="/products/:productId/calculator" element={<ProductRateCalculator />} />
          <Route path="/mypage" element={<MyPageRoute />} />

          <Route path="/" element={<Introduce />} />

          <Route path="/terms" element={
            <div className="min-h-screen bg-[#EFFFFD]">
              <Agreement />
            </div>
          } />
        </Routes>
      </AuthGate>
      <Footer />
    </BrowserRouter>
  );
}

export default App;
