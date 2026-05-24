import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// 전문보기 모달
const ContentModal = ({ term, onClose }) => {
  const parseContent = (text) => {
    return text.split(/(\*\*.*?\*\*)/).map((part, i) =>
      part.startsWith('**') && part.endsWith('**')
        ? <strong key={i}>{part.slice(2, -2)}</strong>
        : part
    );
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-[70]" onClick={onClose}>
      <div className="bg-white font-[Inter] text-[#515151] w-full mt-5 max-w-2xl p-9 rounded-2xl shadow-2xl relative" onClick={e => e.stopPropagation()}>
        <button onClick={onClose} className="absolute top-6 right-6 text-gray-400 hover:text-gray-600 text-lg font-bold">✕</button>
        <h3 className="text-lg font-bold mb-4">{term.title}</h3>
        <p className="text-sm whitespace-pre-wrap leading-relaxed overflow-y-auto max-h-[60vh]">{parseContent(term.content)}</p>
      </div>
    </div>
  );
};

// 개별 약관 항목
const AgreementItem = ({ id, label, checked, onChange, onView }) => (
  <div className="flex items-center justify-between p-4 bg-[#EFEFEF] rounded-lg mb-2">
    <div className="flex items-center gap-3">
      <div
        onClick={() => onChange(id)}
        className={`w-5 h-5 rounded-sm border-2 flex items-center justify-center cursor-pointer transition-colors ${
          checked ? 'bg-[#03BFA5] border-[#03BFA5]' : 'bg-white border-white'
        }`}
      >
        {checked && (
          <svg className="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        )}
      </div>
      <span className="text-sm">{label}</span>
    </div>
    {onView && (
      <button onClick={onView} className="text-xs text-[#515151] underline hover:text-gray-400 transition-colors">
        전문보기
      </button>
    )}
  </div>
);

function Agreement() {
  const { accessToken } = useAuth();
  const navigate = useNavigate();
  const [terms, setTerms] = useState([]);
  const [checks, setChecks] = useState({ age: false });
  const [selectedTerm, setSelectedTerm] = useState(null);

  const isAllChecked = Object.values(checks).every(Boolean);

  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = ''; };
  }, []);

  useEffect(() => {
    if (!accessToken) return;
    const fetchTerms = async () => {
      try {
        const res = await fetch("https://test-fin.duckdns.org/term", {
          headers: { Authorization: `Bearer ${accessToken}` }
        });
        const data = await res.json();
        setTerms(data);
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

  const handleAll = () => {
    const nextVal = !isAllChecked;
    const newChecks = { age: nextVal };
    terms.forEach(t => { newChecks[t.code] = nextVal; });
    setChecks(newChecks);
  };

  const handleSingle = (id) => {
    setChecks(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const handleNext = async () => {
    try {
      const res = await fetch("https://test-fin.duckdns.org/term/agree", {
        method: "POST",
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!res.ok) throw new Error("약관 동의 실패");
      navigate('/');
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <>
      {/* 전문보기 모달 */}
      {selectedTerm && <ContentModal term={selectedTerm} onClose={() => setSelectedTerm(null)} />}

      {/* 약관 동의 모달 */}
      <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-[60]">
        <div className="bg-white font-[Inter] text-[#515151] w-full mt-5 max-w-2xl p-9 rounded-2xl shadow-2xl">
          <h2 className="text-xl font-bold mb-5 mt-6">이용약관에 동의해주세요.</h2>

          {/* 전체 동의 */}
          <div className="p-6 bg-[#EFEFEF] rounded-xl mb-6">
            <div className="flex items-center gap-3 mb-1">
              <div
                onClick={handleAll}
                className={`w-6 h-6 rounded-sm border-2 flex items-center justify-center cursor-pointer transition-colors ${
                  isAllChecked ? 'bg-[#03BFA5] border-[#03BFA5]' : 'bg-white border-white'
                }`}
              >
                {isAllChecked && (
                  <svg className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                  </svg>
                )}
              </div>
              <span className="font-bold text-lg">전체 동의</span>
            </div>
            <p className="text-sm ml-9">필수 및 선택 약관에 모두 동의합니다.</p>
          </div>

          {/* 개별 약관 */}
          <div className="space-y-1">
            <AgreementItem id="age" label="[필수] 만 14세 이상입니다." checked={checks.age} onChange={handleSingle} />
            {terms.map(t => (
              <AgreementItem
                key={t.id}
                id={t.code}
                label={t.title}
                checked={checks[t.code] ?? false}
                onChange={handleSingle}
                onView={() => setSelectedTerm(t)}
              />
            ))}
          </div>

          <button
            disabled={!isRequiredFilled}
            onClick={handleNext}
            className={`w-full mt-10 py-5 rounded-lg font-medium text-xl transition-all shadow-lg ${
              isRequiredFilled
                ? 'bg-[#03BFA5] text-white hover:bg-[#30E6CD] active:scale-95 cursor-pointer'
                : 'bg-[#A0A1A0] text-white cursor-not-allowed'
            }`}
          >
            다음
          </button>
        </div>
      </div>
    </>
  );
}

export default Agreement;