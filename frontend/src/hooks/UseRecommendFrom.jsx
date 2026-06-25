import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";

const BANK_CATEGORIES = [
  { id: '시중', title: '시중은행', banks: ['KB국민', '신한', '하나', '우리', 'SC제일', 'iM뱅크'] },
  { id: '인터넷', title: '인터넷은행', banks: ['카카오뱅크', '토스뱅크', '케이뱅크'] },
  { id: '특수', title: '특수은행', banks: ['NH농협', 'Sh수협', 'IBK기업'] },
  { id: '지방', title: '지방은행', banks: ['BNK부산', '광주은행', '제주은행', '전북은행', 'BNK경남은행'] },
];

const INCOME_LEVELS = [
  { label: "중위소득 60%", amount: "월 154만원 이하" },
  { label: "중위소득 80%", amount: "월 205만원 이하" },
  { label: "중위소득 100%", amount: "월 256만원 이하" },
  { label: "중위소득 120%", amount: "월 308만원 이하" },
  { label: "중위소득 150%", amount: "월 385만원 이하" },
  { label: "중위소득 180%", amount: "" },
];

const MOCK_CATEGORIES = {
  regions: [
    { optionId: "mock_seoul", optionValue: "서울특별시" },
    { optionId: "mock_busan", optionValue: "부산광역시" },
    { optionId: "mock_ulsan", optionValue: "울산광역시" },
    { optionId: "mock_gyeongnam", optionValue: "경상남도" },
    { optionId: "mock_gwangju", optionValue: "광주광역시" },
    { optionId: "mock_jeonnam", optionValue: "전라남도" },
    { optionId: "mock_jeonbuk", optionValue: "전북특별자치도" },
    { optionId: "mock_jeju", optionValue: "제주특별자치도" },
  ],
  status: [
    { optionId: "mock_student", optionValue: "학생" },
    { optionId: "mock_worker", optionValue: "재직자" },
  ],
  savingPeriod: [
    { optionId: "mock_short", optionValue: "1년 내외(단기)" },
    { optionId: "mock_long", optionValue: "3년 초과(장기)" },
  ],
  benefits: [
    { optionId: "mock_rate", optionValue: "우대금리" },
    { optionId: "mock_support", optionValue: "정부지원" },
  ],
  bankRelation: [
    { optionId: "mock_first", optionValue: "첫 거래" },
    { optionId: "mock_salary", optionValue: "급여 이체" },
  ],
  bankCategories: BANK_CATEGORIES,
  incomeLevel: INCOME_LEVELS,
};

function isMockRecommendMode() {
  return import.meta.env.DEV
    && new URLSearchParams(window.location.search).get("mock") === "true";
}

export default function useRecommendForm() {
  const { accessToken } = useAuth();
  const [step, setStep] = useState(0);
  const [formData, setFormData] = useState({});
  const [cats, setCats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isMockRecommendMode()) {
      setCats(MOCK_CATEGORIES);
      setLoading(false);
      return;
    }

    if (!accessToken) return; // 토큰 없으면 대기... 인데 백엔드에서 수정 시 바꿈
    const fetchCategories = async () => {
    try {
      const res = await fetch("https://test-fin.duckdns.org/api/categories", {
        headers: { Authorization: `Bearer ${accessToken}` },
      });

      if (!res.ok) throw new Error("카테고리 요청 실패");

      const data = await res.json();

      setCats({
        regions: data.find((c) => c.categoryName === "거주지역")?.options || [],
        status: data.find((c) => c.categoryName === "현재신분")?.options || [],
        savingPeriod: data.find((c) => c.categoryName === "저축기간")?.options || [],
        benefits: data.find((c) => c.categoryName === "핵심혜택")?.options || [],
        bankRelation: data.find((c) => c.categoryName === "은행거래")?.options || [],
        bankCategories: BANK_CATEGORIES,
        incomeLevel: INCOME_LEVELS,
      });
    } catch (e) {
      console.error("카테고리 불러오기 실패:", e);
    } finally {
      setLoading(false);
    }
  };

  fetchCategories();
  }, [accessToken]);
  
  // 서버에 보내기는 나중에...
  const handleSubmit = async () => {
    /*
    try {
      const res = await fetch("https://test-fin.duckdns.org/api/recommend", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(formData),
      });
      
      if (!res.ok) throw new Error(“전송 실패”);
      
      const result = await res.json();
      console.log("추천 결과:", result);
    } catch (e) {
      console.error(e);
    }
    */
  };

  const go = (n) => () => setStep(n);

  return { step, formData, setFormData, cats, loading, go, handleSubmit };
}
