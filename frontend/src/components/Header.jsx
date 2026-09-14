import client from '../api/client'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import logo from '../assets/logo.png'
import { clearPersistedRecommendation, readPersistedRecommendation } from '../utils/recommendationResult'

export default function Header() {
  return (
    <header className="sticky top-0 z-50 w-full bg-white">
      <div className="mx-auto flex h-14 max-w-480 items-center justify-between px-4 sm:h-16 sm:px-6 lg:h-18 lg:px-[3.5%]">
        <Logo />
        <NavMenu />
        <UserButtons />
      </div>
    </header>
  )
}

function Logo() {
  const navigate = useNavigate()
  return (
    <img src={logo} alt="Fin 로고" className="h-6 shrink-0 cursor-pointer sm:h-7" onClick={() => navigate('/')} />
  )
}

const navItems = [
  { label: '서비스 소개', path: '/introduce' },
  { label: '금융상품 추천', path: '/recommend', activePaths: ['/recommend', '/products'] },
  { label: '정보 커뮤니티', path: '/community' },
  { label: '마이페이지', path: '/mypage' },
]

function NavMenu() {
  const navigate = useNavigate()
  const location = useLocation()

  const handleNavClick = (item) => {
    if (item.path === '/recommend' && readPersistedRecommendation()?.result) {
      navigate('/products')
      return
    }
    navigate(item.path)
  }

  return (
    <ul className="hidden flex-1 items-center justify-between px-20 md:flex lg:px-30 xl:px-50">
      {navItems.map((item, i) => {
        const paths = item.activePaths ?? [item.path]
        const isActive = paths.some((p) => location.pathname === p || location.pathname.startsWith(`${p}/`))
        return (
          <li
            key={i}
            onClick={() => handleNavClick(item)}
            className={`cursor-pointer whitespace-nowrap font-pretendard text-[16px] font-medium transition-colors lg:text-[18px] ${
              isActive ? 'text-[#03BFA5]' : 'text-gray-400 hover:text-[#515151]'
            }`}
          >
            {item.label}
          </li>
        )
      })}
    </ul>
  )
}

function UserButtons() {
  const { accessToken, setAccessToken } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    try {
      await client.post('/auth/logout', {})
    } catch (e) {
      console.error(e)
    } finally {
      clearPersistedRecommendation()
      setAccessToken(null)
      navigate('/login')
    }
  }

  if (accessToken) {
    return (
      <div className="flex shrink-0 items-center gap-3 font-pretendard text-[14.5px]">
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
    <div className="flex shrink-0 items-center gap-3 font-pretendard text-[14.5px]">
      <LoginButton />
      <JoinButton />
    </div>
  )
}

function LoginButton() {
  const navigate = useNavigate()
  return (
    <button
      type="button"
      onClick={() => navigate('/login')}
      className="text-[#515151] border border-gray-300 rounded-lg h-9 w-19.5 hover:border-[#03BFA5] hover:text-[#03BFA5] transition-colors whitespace-nowrap"
    >
      로그인
    </button>
  )
}

function JoinButton() {
  const navigate = useNavigate()
  return (
    <button
      type="button"
      onClick={() => navigate('/terms')}
      className="text-white bg-[#03BFA5] rounded-lg h-9 w-19.5 hover:bg-[#02a892] transition-colors whitespace-nowrap"
    >
      회원가입
    </button>
  )
}
