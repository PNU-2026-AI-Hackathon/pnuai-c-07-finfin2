import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { AuthProvider } from './context/AuthContext';
import App from './App.jsx'
import './index.css'

createRoot(document.getElementById('root')).render(
  // StrictMode때문에 리프레쉬 토근 요청 두 번 가서 일단 꺼뒀는데 나중에 켜야 함
  //<StrictMode>
  <AuthProvider>
    <App />
  </AuthProvider>
  //</StrictMode>,
)
