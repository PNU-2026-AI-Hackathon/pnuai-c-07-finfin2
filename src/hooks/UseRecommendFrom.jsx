import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";

export default function useRecommendForm() {
  const { accessToken } = useAuth();
  const [step, setStep] = useState(0);
  const [formData, setFormData] = useState({});
  const [cats, setCats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
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
        banks: ["은행임시1", "은행임시2", "은행임시3"],
        incomeLevel: [
          { label: "중위소득 60%", amount: "월 154만원 이하" },
          { label: "중위소득 80%", amount: "월 205만원 이하" },
          { label: "중위소득 100%", amount: "월 256만원 이하" },
          { label: "중위소득 120%", amount: "월 308만원 이하" },
          { label: "중위소득 150%", amount: "월 385만원 이하" },
          { label: "중위소득 180%", amount: "" },
        ],
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