import bubbleBg from '../assets/background_bubble.png'
import keyImg from '../assets/keyImg.png'
import googleIcon from '../assets/GoogleIcon.png'
import kakaoIcon from '../assets/KakaoIcon.png'

export default function Login() {
  return (
    <div
      className="min-h-screen bg-cover bg-center bg-no-repeat flex flex-col"
      style={{ backgroundImage: `url(${bubbleBg})` }}
    >
      <main className="flex-1 flex flex-col items-center justify-end pb-[5%]">
        <img src={keyImg} alt="열쇠" className="w-90" />
        <TextAndButtons />
      </main>
    </div>
  )
}

function TextAndButtons() {
  return (
    <div className="flex flex-col items-center gap-3 w-fit">
      <Text />
      <AuthButtons />
    </div>
  )
}

function Text() {
  return (
    <div className="text-5xl font-[Abhaya_Libre] font-medium text-[#515151] pb-10">
      <h1>청년들의 <span className="font-bold">금융 고민을</span> 끝내다.</h1>
    </div>
  )
}

function AuthButtons() {
  return (
    <div className="flex flex-col gap-3 w-full">
      <KakaoButton />
      <GoogleButton />
    </div>
  )
}

function KakaoButton() {
  const handleKakaoLogin = () => {
    window.location.href = "https://test-fin.duckdns.org/oauth2/authorization/kakao";
  };
  return (
    <button
      onClick={handleKakaoLogin}
      className="flex items-center justify-center gap-2 bg-[#FEE500] hover:bg-yellow-200 text-[#51515] py-2.5 rounded-full transition-colors">
      <img src={kakaoIcon} alt="카카오" className="w-5" />
      <span className="font-[Inter] font-semibold text-[#515151] relative top-px">카카오로 시작하기</span>
    </button>
  )
}

function GoogleButton() {
  const handleGoogleLogin = () => {
    window.location.href = "https://test-fin.duckdns.org/oauth2/authorization/google";
  };
  return (
    <button
    onClick={handleGoogleLogin}
    className="flex items-center justify-center gap-2 bg-white hover:bg-gray-100 text-[#51515] py-2.5 rounded-full transition-colors">
      <img src={googleIcon} alt="구글" className="w-5 h-5" />
      <span className="font-[Inter] font-semibold text-[#515151]  relative top-px">구글로 시작하기</span>
    </button>
  )
}
