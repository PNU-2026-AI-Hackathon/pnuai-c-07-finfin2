import axios from 'axios'
import { useNavigate, useLocation } from 'react-router-dom'
import {useAuth} from '../context/AuthContext'
import logo from '../assets/logo.png'

export default function Header() {
  return (
    <header className="sticky top-0 w-full h-18 flex items-center px-[3.5%] bg-white z-50">
      <Logo />
      <NavMenu />
      <div className="ml-auto">
        <UserButtons />
      </div>
    </header>
  )
}

function Logo() {
  return (
    <img src={logo} alt="Fin 로고" className="h-7 cursor-pointer" onClick={() => navigate('/')} />
  )
}

const navItems = [
  { label: '서비스 소개', path: '/introduce' },
  { label: '금융상품 추천', path: '/recommend' },
  { label: '정보 커뮤니티', path: '/community' },
  { label: '마이페이지', path: '/mypage' },
]

function NavMenu() {
  const navigate = useNavigate()
  const location = useLocation()
  return (
    <ul className="flex gap-[160px] ml-[300px]">
      {navItems.map((item, i) => (
        <li
          key={i}
          onClick={() => navigate(item.path)}
          className={`font-[Inter] font-medium text-[17px] cursor-pointer whitespace-nowrap transition-colors
            ${location.pathname === item.path
              ? 'text-[#03BFA5]'
              : 'text-gray-400 hover:text-[#515151]'
            }`}
        >
          {item.label}
        </li>
      ))}
    </ul>
  )
}

function UserButtons() {
  const { accessToken, setAccessToken } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
  try {
    await axios.post('https://test-fin.duckdns.org/auth/logout', {}, { withCredentials: true })
  } catch (e) {
    console.error(e)
  } finally {
    setAccessToken(null)
    navigate('/login')
  }
}

  if (accessToken) {
    return (
      <div className="flex items-center gap-3 font-inter text-[14.5px]">
        <button
          onClick={() => navigate('/mypage')}
          className="text-[#515151] border border-gray-300 rounded-lg h-9 px-4 hover:border-[#03BFA5] hover:text-[#03BFA5] transition-colors whitespace-nowrap"
        >
          프로필
        </button>
        <button
          onClick={handleLogout}
          className="text-white bg-[#03BFA5] rounded-lg h-9 px-4 hover:bg-[#02a892] transition-colors whitespace-nowrap"
        >
          로그아웃
        </button>
      </div>
    )
  }

  return (
    <div className="flex items-center gap-3 font-inter text-[14.5px]">
      <LoginButton />
      <JoinButton />
    </div>
  )
}

function LoginButton() {
  return (
    <button className="text-[#515151] border border-gray-300 rounded-lg h-9 w-19.5 hover:border-[#03BFA5] hover:text-[#03BFA5] transition-colors whitespace-nowrap">
      로그인
    </button>
  )
}

function JoinButton() {
  return (
    <button className="text-white bg-[#03BFA5] rounded-lg h-9 w-19.5 hover:bg-[#02a892] transition-colors whitespace-nowrap">
      회원가입
    </button>
  )
}