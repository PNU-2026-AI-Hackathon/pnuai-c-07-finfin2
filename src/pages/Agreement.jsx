import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

//개별 약관 항목 컴포넌트 
const AgreementItem = ({ id, label, checked, onChange }) => (
  <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl mb-2">
    <div className="flex items-center gap-3">
      <input 
        type="checkbox" 
        checked={checked}
        onChange={() => onChange(id)}
        className="w-5 h-5 accent-slate-500 cursor-pointer" 
      />
      <span className="text-sm text-gray-700 font-medium">{label}</span>
    </div>
    <button className="text-xs text-gray-400 underline hover:text-gray-600 transition-colors">
      전문보기
    </button>
  </div>
);

function Agreement() {
  const { accessToken } = useAuth();
  const [terms, setTerms] = useState([]);
  const [checks, setChecks] = useState({ age: false });

  const isAllChecked = Object.values(checks).every(Boolean);

  // 약관 목록 api 조회
  useEffect(() => {
    if (!accessToken) return;
    const fetchTerms = async () => {
      try {
        const res = await fetch("https://test-fin.duckdns.org/term", {
          headers: { Authorization: `Bearer ${accessToken}` }
        });
        const data = await res.json();
        setTerms(data);
        // API에서 받은 항목들 checks에 동적으로 추가
        const initialChecks = { age: false };
        data.forEach(t => { initialChecks[t.code] = false; });
        setChecks(initialChecks);
      } catch (e) {
        console.error(e);
      }
    };
    fetchTerms();
  }, [accessToken]);

  const isRequiredFilled = checks.age &&
  terms.filter(t => t.code === 'SERVICE_TERMS' || t.code === 'PRIVACY_POLICY')
      .every(t => checks[t.code]);

  //전체 동의 핸들러
  const handleAll = () => {
    const nextVal = !isAllChecked;
    const newChecks = { age: nextVal };
    terms.forEach(t => { newChecks[t.code] = nextVal; });
    setChecks(newChecks);
  };

  const handleSingle = (id) => {
    setChecks(prev => ({ ...prev, [id]: !prev[id] }));
  };

  //사이드바 메뉴(example)
  const sidebarMenus = [
    { id: 'home', label: 'Home', icon: '🏠' },
    { id: 'search', label: 'Search', icon: '🔍' },
    { id: 'profile', label: 'Profile', icon: '👤' },
    { id: 'settings', label: 'Settings', icon: '⚙️' },
  ];

  const handleNext = async () => {
    // try {
    //   const res = await fetch("https://test-fin.duckdns.org/term/agree", {
    //     method: "POST",
    //     headers: { Authorization: `Bearer ${accessToken}` },
    //   });
    //   if (!res.ok) throw new Error("약관 동의 실패");
    //   navigate('/');
    // } catch (e) {
    //   console.error(e);
    // }
  };

  return (
    <div className="flex min-h-screen bg-[#EAF8F0] font-inter text-[22px] text-slate-900">
      

      {/* 메인 영역 */}
      <main className="flex-1 flex flex-col">
        {/*콘텐츠 배치*/}
        <div className="flex-1 flex justify-center items-center p-6">
          <div className="w-full max-w-xl bg-white p-12 rounded-[40px] shadow-2xl">
            <h2 className="text-2xl font-extrabold mb-8 text-slate-800">이용약관에 동의해주세요.</h2>

            {/*전체 동의 박스*/}
            <div className="p-6 bg-slate-100 rounded-2xl mb-6">
              <div className="flex items-center gap-3 mb-1">
                <input 
                  type="checkbox" 
                  checked={isAllChecked} 
                  onChange={handleAll}
                  className="w-6 h-6 accent-slate-600 cursor-pointer" 
                />
                <span className="font-bold text-lg text-slate-800">전체 동의</span>
              </div>
              <p className="text-xs text-slate-400 ml-9">필수 및 선택 약관에 모두 동의합니다.</p>
            </div>

            {/*개별 약관 리스트*/}
            <div className="space-y-1">
              <AgreementItem id="age" label="[필수] 만 14세 이상입니다." checked={checks.age} onChange={handleSingle} />
                {terms.map(t => (
              <AgreementItem key={t.id} id={t.code} label={t.title} checked={checks[t.code] ?? false} onChange={handleSingle} />
                ))}
            </div>

            <button 
              disabled={!isRequiredFilled}
              onClick={handleNext}
              className={`w-full mt-10 py-5 rounded-2xl font-black text-xl transition-all shadow-lg ${
                isRequiredFilled 
                ? 'bg-slate-700 text-white hover:bg-slate-800 active:scale-95 cursor-pointer' 
                : 'bg-slate-300 text-slate-500 cursor-not-allowed'
              }`}
            >
              다음
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}

export default Agreement;