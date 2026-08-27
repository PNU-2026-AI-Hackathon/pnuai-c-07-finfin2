import { useEffect, useMemo, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import InfoIcon from "../components/InfoIcon";
import { TopCard, ListItem } from "../components/ProductComponents";
import icon_1_fin_sector from "../assets/icon_1_fin_sector.png";
import icon_gov_support from "../assets/icon_gov_support.png";
import icon_subscription from "../assets/icon_subscription.png";
import {
  buildProductListFromResult,
  getActiveRecommendationResult,
} from "../utils/recommendationResult";

const SECTIONS = [
  { name: "정부 청년 상품", filterKey: "정부 지원" },
  { name: "시중 은행 상품 · 제 1금융권", filterKey: "제 1금융권" },
  { name: "청약 상품", filterKey: "청약" },
];

export default function ProductList() {
  const { accessToken } = useAuth();
  const isLoggedIn = Boolean(accessToken);
  const navigate = useNavigate();
  const location = useLocation();
  const sectionListRef = useRef(null);
  const recommendationResult = useMemo(
    () => getActiveRecommendationResult(location),
    [location],
  );
  const rateTabEnabled = recommendationResult?.tabs?.tabBEnabled === true;
  const rateTabDisabledReason = recommendationResult?.tabs?.tabBDisabledReason;

  useEffect(() => {
    if (!recommendationResult) {
      navigate("/recommend", { replace: true });
    }
  }, [recommendationResult, navigate]);

  const [searchTerm, setSearchTerm] = useState("");
  const [activeTab, setActiveTab] = useState("나에게 맞는 순");
  const [activeFilter, setActiveFilter] = useState("전체");
  const [blockedModalReason, setBlockedModalReason] = useState(null); // "login" | "profile" | null
  const effectiveActiveTab = !rateTabEnabled && activeTab === "내가 받을 수 있는 금리 순" ? "나에게 맞는 순" : activeTab;
  const recommendationProducts = useMemo(
    () => buildProductListFromResult(recommendationResult),
    [recommendationResult],
  );

  // eligibleProductCount: 백엔드가 조건 매칭을 검토한 전체 후보 상품 수.
  // recommendationProducts: 그중 조건을 충족해 실제로 추천 목록에 포함된 상품 수.
  // eligibleProductCount가 응답에 없으면 추천 개수를 총 개수로 대체해 미충족 0개로 표시한다.
  const recommendationCount = recommendationResult ? recommendationProducts.length : null;
  const totalCandidateCount = recommendationResult?.eligibleProductCount ?? recommendationCount;
  const excludedCount = totalCandidateCount !== null && recommendationCount !== null
    ? Math.max(0, totalCandidateCount - recommendationCount)
    : null;

  const filters = [
    { label: "전체", icon: null },
    { label: "정부 지원", icon: icon_gov_support },
    { label: "제 1금융권", icon: icon_1_fin_sector },
    { label: "청약", icon: icon_subscription },
  ];

  const handleTabClick = (tabName) => {
    if (tabName === "내가 받을 수 있는 금리 순" && !rateTabEnabled) {
      setBlockedModalReason(isLoggedIn ? "profile" : "login");
      return;
    }
    setActiveTab(tabName);
  };

  const goToProductDetail = (productId) => {
    navigate(`/products/${productId}`);
  };

  const scrollToSectionList = () => {
    if (!sectionListRef.current) return;
    const top = sectionListRef.current.getBoundingClientRect().top + window.scrollY;
    window.scrollTo({ top, behavior: "smooth" });
  };

  const processedProducts = useMemo(() => {
    const normalizedSearch = searchTerm.trim().toLowerCase();
    return recommendationProducts.filter((product) => {
      const matchesSearch =
        !normalizedSearch ||
        [product.title, product.subtitle, product.institution].some((value) => value?.toLowerCase().includes(normalizedSearch));
      const matchesFilter =
        activeFilter === "전체" ||
        SECTIONS.some((section) => section.filterKey === activeFilter && section.name === product.category);
      return matchesSearch && matchesFilter;
    }).sort((a, b) =>
      effectiveActiveTab === "나에게 맞는 순" ? b.suitability - a.suitability : parseFloat(b.myRate) - parseFloat(a.myRate),
    );
  }, [activeFilter, effectiveActiveTab, recommendationProducts, searchTerm]);

  const topThree = processedProducts.slice(0, 3);
  const remainingProducts = processedProducts.slice(3);

  if (!recommendationResult) return null;

  return (
    <div className="w-full bg-white pb-[80px] select-none font-pretendard px-[clamp(16px,3vw,40px)]">

      {/* 상단 고정 영역: 검색창 + 탭 + 필터 */}
      <div className="bg-white">

        {/* 상단 고정 검색바 영역 */}
        <div className="bg-white mt-10 mb-15 shrink-0">
          <div className="max-w-[950px] mx-auto ">
            <div className="relative w-full">
              <svg className="absolute left-6 top-1/2 -translate-y-1/2 w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
              </svg>
              <input
                type="text" placeholder="상품명으로 검색하기" value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full h-[clamp(56px,5.5vw,70px)] pl-[clamp(40px,5vw,64px)] pr-[clamp(40px,5vw,64px)] rounded-full border-2 border-[#03BFA5] text-[clamp(16px,1.4vw,19px)] text-[#A4BAB2] placeholder-gray-400 focus:outline-none shadow-sm bg-white"
              />
              <button className="absolute right-2.5 top-1/2 -translate-y-1/2 w-[38px] h-[38px] rounded-full bg-[#03BFA5] flex items-center justify-center hover:bg-[#02A68F] transition-colors">
                <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 12h14M12 5l7 7-7 7" /></svg>
              </button>
            </div>
          </div>
        </div>

        <div className="max-w-370 w-full mx-auto flex flex-col">

          <div className="flex items-end gap-4 h-auto relative z-50">

            {/* 탭 버튼 묶음 */}
            <div className="flex items-end mb-[-1px] relative z-10">
              <button
                onClick={() => handleTabClick("나에게 맞는 순")}
                className={`px-3 h-8 flex items-center justify-center text-[15px] rounded-tl-sm transition-all border-[2px] border-r-0 ${
                  effectiveActiveTab === "나에게 맞는 순"
                    ? "bg-[#03BFA5] text-white border-[#03BFA5]"
                    : "text-[#03BFA5] bg-white border-[#03BFA5]"
                }`}
              >
                나에게 맞는 순
              </button>
              <button
                onClick={() => handleTabClick("내가 받을 수 있는 금리 순")}
                className={`px-3 h-8 flex items-center justify-center gap-1.5 text-[15px] rounded-tr-[2px] transition-all border-[2px] ${
                  effectiveActiveTab === "내가 받을 수 있는 금리 순"
                    ? "bg-[#03BFA5] text-white border-[#03BFA5]"
                    : "text-[#03BFA5] bg-white border-[#03BFA5]"
                }`}
              >
                {!rateTabEnabled && (
                  <svg className="w-4 h-4 text-[#03BFA5]" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                    <rect x="5" y="11" width="14" height="10" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                )}
                내가 받을 수 있는 금리 순
              </button>
            </div>

            <div className="flex items-center gap-1 pb-1">
              <span className="text-[15px] text-[#03BFA5] tracking-tight">
                {effectiveActiveTab === "나에게 맞는 순" ? "적합도란?" : "달성 가능 금리란?"}
              </span>

              <InfoIcon
                text={
                  effectiveActiveTab === "나에게 맞는 순"
                    ? "선택하신 키워드(핵심 혜택 · 저축 기간 · 현재 신분 · 은행 거래)를 기준으로 산정한 매칭 점수예요.\n정부 지원 상품과 제 1금융권 상품별로 가중치를 다르게 반영하여 산정했어요."
                    : "입력하신 정보(소득 · 근속 · 주거래 은행 등)와 단계 1 키워드를 기반으로 실제로 받을 수 있는 우대조건만 적용한 실질 금리에요.\n정부 상품과 시중은행 상품의 계산 방식이 달라요."
                }
              />
            </div>

          </div>

          <div className="bg-white rounded-t-xl rounded-tl-none border-[1.5px] border-b-0 border-[#EBEBEB] px-[clamp(24px,4vw,64px)] pt-[clamp(12px,1.8vw,20px)]">

            {/* 필터 버튼 바 */}
            <div className="p-[clamp(16px,2.2vw,24px)] pb-4 shrink-0 bg-white">
              <div className="flex items-center justify-between gap-2 flex-wrap">
                <div className="flex gap-2">
                  {filters.map(filter => (
                    <button key={filter.label} onClick={() => setActiveFilter(filter.label)} className={`flex items-center gap-1.5 px-3 py-1 rounded-md border text-[15px] font-medium transition-colors ${activeFilter === filter.label ? "border-[2px] border-[#03BFA5] text-[#03BFA5] bg-[#F0FFFE] shadow-sm" : "border-[2px] border-[#E0DFDF] text-[#454545] bg-white hover:bg-gray-50"}`}>
                      {filter.icon && <img src={filter.icon} alt="" className="w-4 h-4" />}
                      {filter.label}
                    </button>
                  ))}
                </div>
                <button
                  type="button"
                  onClick={() => {
                    if (isLoggedIn) {
                      navigate("/mypage", { state: { activeTab: "info" } });
                    } else {
                      navigate("/recommend", { state: { openForm: true } });
                    }
                  }}
                  className="flex items-center gap-1.5 px-4 py-1.5 rounded-sm border-[2px] border-[#E0DFDF] bg-white text-[15px] text-[#454545] font-medium hover:bg-gray-50 hover:shadow-sm whitespace-nowrap"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" /></svg>
                  입력 정보 수정
                </button>
              </div>

              {/* 안내 문구 */}
              <div className="flex items-center gap-1.5 mt-5 text-[15px] font-medium text-[#03BFA5] text-[15px] font-bold">
                {rateTabEnabled ? (
                  <>
                    <div className="w-[16px] h-[16px] rounded-full bg-[#03BFA5] text-white flex items-center justify-center">✓</div>
                    <span>
                      {recommendationCount === null
                        ? "입력 정보를 바탕으로 나에게 맞는 상품을 정렬했어요."
                        : `총 ${totalCandidateCount}개 상품 중 미충족 ${excludedCount}개를 제외한 ${recommendationCount}개의 상품을 추천했어요.`}
                    </span>
                  </>
                ) : (
                  <><div className="w-[16px] h-[16px] rounded-full bg-[#03BFA5] text-white flex items-center justify-center">i</div><span>{rateTabDisabledReason || (isLoggedIn ? "상세 정보를 입력하면 자격요건 필터링 결과와 내가 받을 수 있는 금리를 확인할 수 있어요." : "로그인하면 자격요건 필터링 결과와 내가 받을 수 있는 금리를 확인할 수 있어요.")}</span></>
                )}
              </div>
            </div>

          </div>

        </div>
      </div>

      {/* 메인 콘텐츠 영역 바구니 */}
      <div className="max-w-370 w-full mx-auto pb-6 flex flex-col">
        <div className="bg-white rounded-b-xl border-[1.5px] border-t-0 border-[#EBEBEB] shadow-sm flex flex-col px-[clamp(24px,4vw,64px)] pb-[clamp(12px,1.8vw,20px)]">

          <div className="p-[clamp(16px,2.2vw,24px)] pt-0 bg-white">

            {/* TOP 3 */}
            <div className="mb-6">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                {topThree.map((product, index) => (
                  <TopCard
                    key={product.id}
                    rank={index + 1}
                    title={product.title}
                    subtitle={product.subtitle}
                    baseRate={product.baseRate}
                    maxRate={product.maxRate}
                    myRate={product.myRate}
                    tags={product.tags}
                    isBest={index === 0}
                    isLoggedIn={rateTabEnabled}
                    contributionRate={product.contributionRate}
                    maturityContribution={product.maturityContribution}
                    contributionCaption={product.contributionCaption}
                    showContribution={product.category === "정부 청년 상품" || product.category === "청약 상품"}
                    onClick={() => goToProductDetail(product.id)}
                  />
                ))}
              </div>
            </div>

            {/* 하단 세로형 분리 목록 */}
            {processedProducts.length === 0 ? (
              <div className="mt-8 flex min-h-[160px] items-center justify-center rounded-[10px] border border-[#E0DFDF] text-[17px] font-medium text-[#606060]">
                검색 결과가 없어요.
              </div>
            ) : (
              <>
                <button
                  type="button"
                  onClick={scrollToSectionList}
                  aria-label="목록 더보기"
                  className="mt-2 flex w-full justify-center"
                >
                  <svg width="32" height="32" viewBox="0 0 53 53" fill="none" aria-hidden="true">
                    <path d="M15 21L26.5 32.5L38 21" stroke="#03BFA5" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </button>
                <div ref={sectionListRef}>
                {SECTIONS.map((sec) => {
                  if (activeFilter !== "전체" && activeFilter !== sec.filterKey) return null;

                  const sectionProducts = remainingProducts.filter(p => p.category === sec.name);
                  if (sectionProducts.length === 0) return null;

                  return (
                    <div key={sec.name} className="mt-8">
                      <h2 className="text-[19px] font-bold text-[#333333] mb-3 ml-1">
                        {sec.name}
                      </h2>
                      <div>
                        {sectionProducts.map((product) => (
                          <ListItem
                            key={product.id}
                            title={product.title}
                            subtitle={product.subtitle}
                            baseRate={product.baseRate}
                            maxRate={product.maxRate}
                            myRate={product.myRate}
                            tags={product.tags}
                            isLoggedIn={rateTabEnabled}
                            variant={sec.name === "정부 청년 상품" ? "contribution" : "rate"}
                            contributionRate={product.contributionRate}
                            maturityContribution={product.maturityContribution}
                            contributionCaption={product.contributionCaption}
                            onClick={() => goToProductDetail(product.id)}
                          />
                        ))}
                      </div>
                    </div>
                  );
                })}
                </div>
              </>
            )}

          </div>
        </div>
      </div>

      {/* 모달 팝업 */}
      {blockedModalReason && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-3xl w-full max-w-[550px] px-22 py-18 text-center shadow-2xl relative">
            <button onClick={() => setBlockedModalReason(null)} className="absolute top-6 right-6 text-gray-400 hover:text-gray-600">
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
            <div className="w-14 h-14 bg-[#F0FFFE] rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-7 h-7 text-[#03BFA5]" fill="currentColor" viewBox="0 0 24 24"><path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/></svg>
            </div>
            {blockedModalReason === "profile" ? (
              <>
                <p className="text-[22px] font-semibold text-[#03BFA5] mb-2">상세 정보 입력 후 이용할 수 있어요</p>
                <p className="text-[13px] text-[#03BFA5] mb-9 leading-relaxed">나에게 맞는 실질 우대금리를<br/>상세 정보 입력 후 바로 확인해보세요.</p>
              </>
            ) : (
              <>
                <p className="text-[22px] font-semibold text-[#03BFA5] mb-2">로그인 후 이용할 수 있어요</p>
                <p className="text-[13px] text-[#03BFA5] mb-9 leading-relaxed">나에게 맞는 실질 우대금리를<br/>로그인 후 바로 확인해보세요.</p>
              </>
            )}
            <div className="flex flex-col gap-2.5 mb-2.5 text-left">
              {["소득·근속·주거래 은행 등 내 정보를 바탕으로 실질 금리를 계산해드려요.", "실제 충족 가능한 우대조건만 적용해 정확한 금리를 보여드려요.","단계2 정보 입력 후 더 정밀한 맞춤 추천이 활성화됩니다."].map((text, i) => (
                <div key={i} className="flex items-start gap-3 border border-[#03BFA5] rounded-lg px-4 py-3 bg-[#F0FFFE]">
                  <div className="mt-2.5 mr-0.5 w-4 h-4 rounded-sm bg-[#03BFA5] flex items-center justify-center shrink-0">
                    <svg className="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" /></svg>
                  </div>
                  <span className="text-[14px] text-[#454545] leading-snug">{text}</span>
                </div>
              ))}
            </div>
            {blockedModalReason === "profile" ? (
              <div className="flex flex-col gap-2.5">
                <button onClick={() => navigate("/mypage", { state: { activeTab: "info" } })} className="w-full py-3 rounded-lg border border-gray-300 text-gray-700 font-medium text-[14px] hover:bg-gray-50">상세 정보 입력하기</button>
              </div>
            ) : (
              <div className="flex flex-col gap-2.5">
                <button onClick={() => navigate("/login")} className="w-full py-3 rounded-lg border border-gray-300 text-gray-700 font-medium text-[14px] hover:bg-gray-50">로그인하고 확인하기</button>
                <button onClick={() => navigate("/terms")} className="w-full py-3 rounded-lg border border-gray-300 text-gray-700 font-medium text-[14px] hover:bg-gray-50">회원가입 후 이용하기</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
