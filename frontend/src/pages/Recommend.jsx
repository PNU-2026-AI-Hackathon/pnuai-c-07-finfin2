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
import { useAuth } from "../context/AuthContext";

// 검색바가 화면 상단에서 이 정도 간격을 유지하도록 스크롤 (sticky 헤더 높이 + 여유 간격)
const SEARCH_BAR_TOP_OFFSET = 96;

function SaveConsentSummary({ expanded, onViewTerms, onContinueWithoutSaving, onSaveAndContinue }) {
  return (
    <div className="flex h-full w-full flex-col px-[41px] pb-[35px] pt-[46px] text-center">
      <div className="mx-auto flex size-[50px] items-center justify-center rounded-full bg-[#EFFFFD]">
        <svg className="h-[27px] w-[25px]" fill="none" viewBox="0 0 36 40" aria-hidden="true">
          <path stroke="#03BFA5" strokeLinejoin="miter" strokeWidth="3.5" d="M6 3h24v33l-12-6-12 6V3Z" />
          <path stroke="#03BFA5" strokeLinecap="square" strokeLinejoin="miter" strokeWidth="3.5" d="m11 19 5 5 9-10" />
        </svg>
      </div>
      <h2 id="profile-save-consent-title" className="mt-3 text-[23px] font-bold leading-[1.35] tracking-[-0.03em] text-[#202020]">
        입력한 정보를 저장할까요?
      </h2>
      <p className="mt-3 text-[14px] leading-[1.55] tracking-[-0.02em] text-[#58635F]">
        저장하면 다음에 자동으로 채워지고,<br />
        마이페이지에서 언제든 수정·삭제할 수 있어요.
      </p>
      <p className="mt-4 text-[14px] leading-[1.5] tracking-[-0.02em] text-[#8C9692]">
        자격 · 조건 정보 12개 항목<br />
        회원 탈퇴 시 보관 기간 없이 바로 파기돼요.
      </p>
      <button
        type="button"
        onClick={onViewTerms}
        className="mt-4 flex h-[44px] w-full shrink-0 cursor-pointer items-center justify-center rounded-[7px] border border-[#03BFA5] bg-[#F2FFFD] text-[16px] font-semibold tracking-[-0.02em] text-[#03BFA5]"
      >
        저장 동의 전문 보기&nbsp;{expanded ? "⌃" : "⌄"}
      </button>
      <div className={`${expanded ? "mt-auto" : "mt-2"} grid shrink-0 grid-cols-2 gap-[6px]`}>
        <button type="button" onClick={onContinueWithoutSaving} className="flex h-[46px] cursor-pointer items-center justify-center rounded-[7px] border border-[#03BFA5] bg-white text-[15px] font-semibold tracking-[-0.02em] text-[#03BFA5]">
          저장 없이 결과 보기
        </button>
        <button type="button" onClick={onSaveAndContinue} className="flex h-[46px] cursor-pointer items-center justify-center rounded-[7px] bg-[#03BFA5] text-[15px] font-semibold tracking-[-0.02em] text-white transition-colors hover:bg-[#02A892]">
          저장하고 결과 보기
        </button>
      </div>
    </div>
  );
}

function SaveConsentTerms() {
  const storedQualifications = [
    ["01", "생년월일"],
    ["02", "거주 지역"],
    ["03", "현재 신분 (재직 형태)"],
    ["04", "근속 기간·첫 직장 여부"],
    ["05", "개인 연 소득"],
    ["06", "가구원 수·가구 소득"],
    ["07", "무주택·세대주 여부"],
    ["08", "거래 이력 (거래 은행)"],
  ];
  const storedPreferences = [
    ["09", "월 납입 희망액"],
    ["10", "저축 기간"],
    ["11", "핵심 혜택 선호"],
    ["12", "은행 거래 우대 선호"],
  ];

  return (
    <div className="h-full w-1/2 rounded-r-[26.5px] bg-[#F0FFFC] px-[40px] py-[42px] text-left">
      <h2 className="text-[22px] font-bold leading-[1.35] tracking-[-0.03em] text-[#171717]">저장 동의 전문</h2>
      <div className="mt-3 h-[538px] overflow-y-scroll pr-3 text-[13px] leading-[1.6] tracking-[-0.02em] text-[#68736F]">
        <p>
          Y-Fin은 이용자의 편의를 위해 아래와 같이 입력 정보를 저장·관리합니다.
          본 동의는 선택 사항이며, 동의하지 않아도 사용자의 프로필에선 결과는 정상적으로 제공됩니다.
        </p>

        <h3 className="mt-4 text-[14px] font-bold tracking-[-0.02em] text-[#03BFA5]">1. 저장 목적</h3>
        <p className="mt-1">다음 진단 시 자동 입력, 마이페이지에서의 조회·수정 편의 제공</p>

        <h3 className="mt-4 text-[14px] font-bold tracking-[-0.02em] text-[#03BFA5]">2. 저장 항목 (12개)</h3>
        <p className="mt-1 font-semibold text-[#4E5B56]">자격·조건 정보 - 8개</p>
        <ul className="mt-1 grid grid-cols-2 gap-x-3 gap-y-1">
          {storedQualifications.map(([number, label]) => (
            <li key={number}><span className="mr-1.5 font-semibold text-[#03BFA5]">{number}</span>{label}</li>
          ))}
        </ul>
        <p className="mt-3 font-semibold text-[#4E5B56]">권리·선호 정보 - 4개</p>
        <ul className="mt-1 grid grid-cols-2 gap-x-3 gap-y-1">
          {storedPreferences.map(([number, label]) => (
            <li key={number}><span className="mr-1.5 font-semibold text-[#03BFA5]">{number}</span>{label}</li>
          ))}
        </ul>

        <h3 className="mt-4 text-[14px] font-bold tracking-[-0.02em] text-[#03BFA5]">3. 보유 및 이용 기간</h3>
        <p className="mt-1">회원 탈퇴 또는 이용자의 삭제 요청 시까지 보관하며, 요청 시 지체 없이 파기합니다.</p>

        <h3 className="mt-4 text-[14px] font-bold tracking-[-0.02em] text-[#03BFA5]">4. 동의 거부 시 안내</h3>
        <p className="mt-1">동의를 거부해도 이번 추천 결과는 확인할 수 있지만, 입력 정보는 저장되지 않아 다음 추천 시 다시 입력해야 합니다.</p>
      </div>
    </div>
  );
}

function ProfileSaveConsentModal({ onClose, onViewTerms, onContinueWithoutSaving, onSaveAndContinue, showTerms }) {
  return (
    <div
      className="fixed inset-0 z-[110] flex items-center justify-center bg-black/40 px-4 py-6"
      role="presentation"
      onMouseDown={onClose}
    >
      <section
        role="dialog"
        aria-modal="true"
        aria-labelledby="profile-save-consent-title"
        className={`flex w-full overflow-hidden rounded-[26.5px] bg-white shadow-2xl ${
          showTerms ? "h-[700px] max-w-[1060px] flex-row" : "h-[438px] max-w-[530px]"
        }`}
        onMouseDown={(event) => event.stopPropagation()}
      >
        {showTerms ? (
          <>
            <div className="w-1/2">
              <SaveConsentSummary
                expanded
                onViewTerms={onViewTerms}
                onContinueWithoutSaving={onContinueWithoutSaving}
                onSaveAndContinue={onSaveAndContinue}
              />
            </div>
            <SaveConsentTerms />
          </>
        ) : (
          <SaveConsentSummary
            onViewTerms={onViewTerms}
            onContinueWithoutSaving={onContinueWithoutSaving}
            onSaveAndContinue={onSaveAndContinue}
          />
        )}
      </section>
    </div>
  );
}

export default function Recommend() {
  const { step, formData, setFormData, cats, loading, go, handleSubmit } = useRecommendForm();
  const { accessToken } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [isOpen, setIsOpen] = useState(Boolean(location.state?.openForm));
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisError, setAnalysisError] = useState("");
  const [analysisResult, setAnalysisResult] = useState(null);
  const [isProfileSaveConsentOpen, setIsProfileSaveConsentOpen] = useState(false);
  const [isProfileSaveTermsOpen, setIsProfileSaveTermsOpen] = useState(false);
  const analysisPromiseRef = useRef(null);
  const searchBarRef = useRef(null);
  const hasPreviousRecommendation = Boolean(readPersistedRecommendation()?.result);
  const isLoggedIn = Boolean(accessToken);

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

  const startAnalysis = ({ saveProfile = false } = {}) => {
    setAnalysisError("");
    setIsOpen(false);
    setIsAnalyzing(true);
    setAnalysisResult(null);

    const analysisPromise = handleSubmit({ saveProfile });
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

  const openProfileSaveConsent = () => {
    setIsProfileSaveTermsOpen(false);
    setIsProfileSaveConsentOpen(true);
  };

  const handleConsentChoice = (saveProfile) => {
    setIsProfileSaveConsentOpen(false);
    startAnalysis({ saveProfile });
  };

  const steps = [
    <StepSavingPlan      data={formData} setData={setFormData} cats={cats} onNext={go(1)} />,
    <StepBasicInfo       data={formData} setData={setFormData} cats={cats} onPrev={go(0)} onNext={go(2)} />,
    <StepBenefits
      data={formData}
      setData={setFormData}
      cats={cats}
      onPrev={go(1)}
      onNext={isLoggedIn ? go(3) : startAnalysis}
    />,
    <StepPersonalInfo    data={formData} setData={setFormData}             onPrev={go(2)} onNext={go(4)} onSkip={startAnalysis} />,
    <StepRegion          data={formData} setData={setFormData} cats={cats} onPrev={go(3)} onNext={go(5)} onSkip={startAnalysis} />,
    <StepHouseholdIncome data={formData} setData={setFormData} cats={cats} onPrev={go(4)} onNext={go(6)} onSkip={startAnalysis} />,
    <StepHousing         data={formData} setData={setFormData}             onPrev={go(5)} onNext={go(7)} onSkip={startAnalysis} />,
    <StepEmployment      data={formData} setData={setFormData}             onPrev={go(6)} onNext={go(8)} onSkip={startAnalysis} />,
    <StepTransaction     data={formData} setData={setFormData} cats={cats} onPrev={go(7)} onSubmit={openProfileSaveConsent} onSkip={startAnalysis} />
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
      {isProfileSaveConsentOpen && (
        <ProfileSaveConsentModal
          showTerms={isProfileSaveTermsOpen}
          onClose={() => setIsProfileSaveConsentOpen(false)}
          onViewTerms={() => setIsProfileSaveTermsOpen((prev) => !prev)}
          onContinueWithoutSaving={() => handleConsentChoice(false)}
          onSaveAndContinue={() => handleConsentChoice(true)}
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
