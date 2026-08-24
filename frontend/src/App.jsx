import { BrowserRouter, Routes, Route } from 'react-router-dom';
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

function App() {
  return (
    <BrowserRouter>
      <ScrollToTop />
      <Header />
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
      <Footer />
    </BrowserRouter>
  );
}

export default App;
