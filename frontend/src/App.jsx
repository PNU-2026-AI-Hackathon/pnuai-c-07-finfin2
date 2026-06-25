import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Header from './components/Header'
import Footer from './components/Footer';
import Login from './pages/Login';
import Main from './pages/Main';
import Introduce from './pages/Introduce';
import Agreement from './pages/Agreement';
import AuthGuard from './routes/AuthGuard';
import Recommend from './pages/Recommend';
import ProductList from './pages/ProductList';

function RecommendRoute() {
  const isMockMode = import.meta.env.DEV
    && new URLSearchParams(window.location.search).get("mock") === "true";

  if (isMockMode) return <Recommend />;
  return <AuthGuard><Recommend /></AuthGuard>;
}

function App() {
  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/introduce" element={<Introduce />} />
        <Route path="/recommend" element={<RecommendRoute />} />
        <Route path="/products" element={<ProductList/>}/>
        
        <Route
          path="/"
          element={
            <AuthGuard>
              <Main />
            </AuthGuard>
          }
        />
        
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
