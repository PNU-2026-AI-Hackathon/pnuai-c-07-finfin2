import useRecommendForm from "../hooks/UseRecommendFrom";
import {
  StepSavingPlan,
  StepRegion,
  StepBasicInfo,
  StepBenefits,
  StepPersonalInfo,
  StepHouseholdIncome,
  StepHousing,
  StepEmployment,
  StepTransaction,
  LoadingScreen,
} from "../components/RecommendSteps";
import { useCallback, useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { readPersistedRecommendation } from "../utils/recommendationResult";

// 검색바가 화면 상단에서 이 정도 간격을 유지하도록 스크롤 (sticky 헤더 높이 + 여유 간격)
const SEARCH_BAR_TOP_OFFSET = 96;

export default function Recommend() {
  const { step, formData, setFormData, cats, loading, go, handleSubmit } = useRecommendForm();
  const navigate = useNavigate();
  const location = useLocation();
  const [isOpen, setIsOpen] = useState(Boolean(location.state?.openForm));
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisError, setAnalysisError] = useState("");
  const [analysisResult, setAnalysisResult] = useState(null);
  const analysisPromiseRef = useRef(null);
  const searchBarRef = useRef(null);
  const hasPreviousRecommendation = Boolean(readPersistedRecommendation()?.result);

  const scrollToSearchBar = useCallback(() => {
    const el = searchBarRef.current;
    if (!el) return;
    const targetTop = el.getBoundingClientRect().top + window.scrollY - SEARCH_BAR_TOP_OFFSET;
    window.scrollTo({ top: Math.max(0, targetTop), behavior: "smooth" });
  }, []);

  // 검색창을 열거나(눌렀을 때) 단계가 바뀔 때마다 검색창이 항상 같은 위치로 오도록 스크롤
  useEffect(() => {
    if (!isOpen) return;
    scrollToSearchBar();
  }, [isOpen, step, scrollToSearchBar]);

  const startAnalysis = () => {
    setAnalysisError("");
    setIsOpen(false);
    setIsAnalyzing(true);
    setAnalysisResult(null);

    const analysisPromise = handleSubmit();
    analysisPromiseRef.current = analysisPromise;
    analysisPromise
      .then((recommendation) => setAnalysisResult(recommendation))
      .catch((error) => {
        console.error("상품 분석 실패:", error);
        setAnalysisError(error.message || "상품 분석에 실패했습니다. 잠시 후 다시 시도해주세요.");
        setIsAnalyzing(false);
        setIsOpen(true);
      });
  };

  const finishAnalysis = useCallback(async () => {
    try {
      const recommendation = analysisResult || await analysisPromiseRef.current;
      navigate("/products", {
        state: {
          recommendationResult: recommendation.result,
          recommendationRequest: recommendation.request,
        },
      });
    } catch (error) {
      console.error("상품 분석 실패:", error);
      setAnalysisError(error.message || "상품 분석에 실패했습니다. 잠시 후 다시 시도해주세요.");
      setIsAnalyzing(false);
      setIsOpen(true);
    }
  }, [analysisResult, navigate]);

  const steps = [
    <StepSavingPlan      data={formData} setData={setFormData} cats={cats} onNext={go(1)} />,
    <StepBasicInfo       data={formData} setData={setFormData} cats={cats} onPrev={go(0)} onNext={go(2)} />,
    <StepBenefits        data={formData} setData={setFormData} cats={cats} onPrev={go(1)} onNext={go(3)} />,
    <StepPersonalInfo    data={formData} setData={setFormData}             onPrev={go(2)} onNext={go(4)} onSkip={startAnalysis} />,
    <StepRegion          data={formData} setData={setFormData} cats={cats} onPrev={go(3)} onNext={go(5)} onSkip={startAnalysis} />,
    <StepHouseholdIncome data={formData} setData={setFormData} cats={cats} onPrev={go(4)} onNext={go(6)} onSkip={startAnalysis} />,
    <StepHousing         data={formData} setData={setFormData}             onPrev={go(5)} onNext={go(7)} onSkip={startAnalysis} />,
    <StepEmployment      data={formData} setData={setFormData}             onPrev={go(6)} onNext={go(8)} onSkip={startAnalysis} />,
    <StepTransaction     data={formData} setData={setFormData} cats={cats} onPrev={go(7)} onSubmit={startAnalysis} onSkip={startAnalysis} />
  ];
  const stepContentScale = 0.8;
  const formVerticalPadding = 85;
  const formBodyMinHeights = [
    654,
    651,
    685,
    737,
    566,
    1126,
    714,
    729,
    1213,
  ];
  const formBodyMinHeight = formBodyMinHeights[step] || 651;
  const stepLayoutMinHeight = Math.max(460, formBodyMinHeight - formVerticalPadding);
  const scaledStepLayoutMinHeight = Math.round(stepLayoutMinHeight * stepContentScale);
  const scaledFormBodyMinHeight = formVerticalPadding + scaledStepLayoutMinHeight;

  return (
    <div className="min-h-screen bg-linear-to-b from-[#F1FFFC] via-[#F8FFFD] to-white flex flex-col">
      {isAnalyzing && (
        <LoadingScreen
          onAnimationComplete={finishAnalysis}
          isAnalysisReady={Boolean(analysisResult)}
          eligibleProductCount={analysisResult?.result?.eligibleProductCount}
        />
      )}

      <div className={`flex-1 flex flex-col items-center px-4 pb-[288px] ${isAnalyzing ? "pt-[146px]" : "pt-[141px]"}`}>

        {/* 타이틀 */}
        <div className={`text-center ${isAnalyzing ? "mb-[253px]" : "mb-[100px]"}`}>
          <h1 className="text-[45px] font-bold leading-[1.15] text-[#4B4B4B] font-gmarket">
            내게 딱 맞는 <span className="text-[#03BFA5]">금융상품,</span>
            {!isAnalyzing && (
              <>
                <br />
                <span className="text-[#03BFA5]">Y-Fin</span>이 찾아줘요
              </>
            )}
          </h1>
          {!isAnalyzing && (
            <p className="text-[14.4px] text-[#A5A5A5] mt-[43px] leading-[1.7] font-gmarket">
              수백 개 은행 상품을 일일이 비교할 필요 없이, 간단히 정보만 입력하면<br />
              최적의 상품을 찾을 수 있어요.
            </p>
          )}
        </div>

        {/* 검색바 + 폼 */}
        <div className="w-full max-w-[962px]">
          <div className={`bg-white shadow-[0_3px_32px_rgba(113,126,123,0.30)] ${
            isOpen ? "rounded-[22px] border border-[#E2E6E5]" : "rounded-full border-2 border-[#03BFA5]"
          }`}>

          {/* 검색바 */}
          <button
            type="button"
            ref={searchBarRef}
            onClick={() => setIsOpen((prev) => !prev)}
            disabled={isAnalyzing}
            aria-label={isOpen ? "정보 입력 닫기" : "정보 입력 열기"}
            className={`w-full h-[67px] flex items-center justify-between px-[23px] hover:bg-gray-50 transition-colors focus:outline-none ${
              isOpen ? "rounded-t-[22px] rounded-b-none" : "rounded-full"
            }`}
          >
            {/* 왼쪽 돋보기 아이콘 */}
            <div className="flex items-center gap-3 text-[#AFC4BF] pl-1">
              <svg className="w-[34px] h-[34px] text-[#AFC4BF]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
              </svg>
              <span className="text-[18px] font-medium">정보를 입력하세요</span>
            </div>
            
            {/* 오른쪽 화살표 버튼 */}
            <div className="w-[44px] h-[44px] rounded-full bg-[#03BFA5] flex items-center justify-center shrink-0">
              <svg className="w-[18px] h-[18px] text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </div>
          </button>

          {/* 폼 */}
          {isOpen && (
            <div
              className="px-[61px] pt-[48px] pb-[37px] border-t border-[#E2E6E5]"
              style={{
                minHeight: `${scaledFormBodyMinHeight}px`,
                "--step-content-scale": stepContentScale,
                "--step-content-width": `${100 / stepContentScale}%`,
                "--step-layout-min-height": `${stepLayoutMinHeight}px`,
                "--scaled-step-layout-min-height": `${scaledStepLayoutMinHeight}px`,
              }}
            >
              {!loading && steps[step]}
              {analysisError && (
                <div
                  role="alert"
                  className="mt-6 rounded-[10px] border border-red-200 bg-red-50 px-5 py-4 text-[16px] text-red-700"
                >
                  {analysisError}
                </div>
              )}
            </div>
          )}

          </div>
          {hasPreviousRecommendation && !isOpen && !isAnalyzing && (
            <div className="mt-[12px] flex justify-end pr-[10px]">
              <button
                type="button"
                onClick={() => navigate("/products")}
                className="flex h-[40px] items-center gap-[6px] rounded-[7px] border border-[#E2E6E5] bg-white px-[14px] text-[15px] font-medium text-[#03BFA5] transition-colors hover:border-[#03BFA5] hover:bg-[#F7FFFE]"
              >
                <svg className="size-[16px]" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 11a8 8 0 1 0 2 5.3M20 4v7h-7" />
                </svg>
                이전 추천 결과 다시보기
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
