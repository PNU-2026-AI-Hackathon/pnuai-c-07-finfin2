import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import client, { withAuth } from "../api/client";
import {
  buildProfileUpdateFromRequest,
  buildRecommendationRequest,
  mapRecommendCategories,
} from "../utils/recommendationPayload";
import { runProductSearch } from "../utils/productSearch";

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
  categoryIds: {},
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
  const mockMode = isMockRecommendMode();

  useEffect(() => {
    if (mockMode) {
      setCats(MOCK_CATEGORIES);
      setLoading(false);
      return;
    }

    const fetchCategories = async () => {
      try {
        const res = await client.get("/api/categories", withAuth(accessToken));
        setCats(mapRecommendCategories(res.data, {
          bankCategories: BANK_CATEGORIES,
          incomeLevel: INCOME_LEVELS,
        }));
      } catch (e) {
        console.error("카테고리 불러오기 실패:", e);
      } finally {
        setLoading(false);
      }
    };

    fetchCategories();
  }, [accessToken, mockMode]);

  const handleSubmit = async () => {
    const request = buildRecommendationRequest(formData, cats);

    if (mockMode) {
      return { request, result: null };
    }

    const recommendationPromise = runProductSearch(request, accessToken);
    const profileSavePromise = accessToken
      ? client
          .put("/user/me/profile", buildProfileUpdateFromRequest(request), withAuth(accessToken))
          .catch((e) => console.error("프로필 저장 실패:", e))
      : Promise.resolve();

    const [recommendation] = await Promise.all([recommendationPromise, profileSavePromise]);

    return recommendation;
  };

  const go = (n) => () => setStep(n);

  return { step, formData, setFormData, cats, loading, go, handleSubmit };
}
