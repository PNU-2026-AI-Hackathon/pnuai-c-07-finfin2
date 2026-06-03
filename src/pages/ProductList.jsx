import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import InfoIcon from "../components/InfoIcon";
import { TopCard, ListItem } from "../components/ProductComponents";
import icon_1_fin_sector from "../assets/icon_1_fin_sector.png";
import icon_gov_support from "../assets/icon_gov_support.png";
import icon_subscription from "../assets/icon_subscription.png";

const INITIAL_PRODUCTS = [
  { 
    id: 1, 
    category: "정부 청년 상품", 
    title: "청년도약계좌", 
    subtitle: "서민금융진흥원 · 5년 만기 · 월 1만~70만원", 
    baseRate: "4.5", 
    maxRate: "6.0", 
    myRate: "5.5", 
    suitability: 88, 
    tags: ["적합도 88%", "비과세", "정부기여금", "장기자산형성"] 
  },
  { 
    id: 2, 
    category: "정부 청년 상품", 
    title: "청년내일저축계좌", 
    subtitle: "보건복지부 · 3년 만기 · 월 10만~30만원", 
    baseRate: "2.0", 
    maxRate: "5.0", 
    myRate: "4.5", 
    suitability: 52, 
    tags: ["적합도 52%", "비과세", "소득조건 충족", "자산형성지원"] 
  },
  { 
    id: 3, 
    category: "정부 청년 상품", 
    title: "장병내일준비적금", 
    subtitle: "국방부 · 병역의무 이행 필수 · 월 최대 40만원", 
    baseRate: "5.0", 
    maxRate: "7.5", 
    myRate: "5.0", 
    suitability: 15, 
    tags: ["적합도 15%", "비과세", "정부재정지원", "군장병특화"] 
  },
  { 
    id: 4, 
    category: "정부 청년 상품", 
    title: "청년희망적금 (만기연계)", 
    subtitle: "서민금융진흥원 · 연계 가입 상품 · 일시납 가능", 
    baseRate: "5.0", 
    maxRate: "6.0", 
    myRate: "5.2", 
    suitability: 40, 
    tags: ["적합도 40%", "비과세 혜택", "청년희망적금 연계"] 
  },
  { 
    id: 5, 
    category: "시중 은행 상품 · 제 1금융권", 
    title: "신한 청년처음적금", 
    subtitle: "신한은행 · 1년 만기 · 월 1만~30만원", 
    baseRate: "3.5", 
    maxRate: "8.0", 
    myRate: "6.5", 
    suitability: 92, 
    tags: ["적합도 92%", "주거래 우대", "첫거래 고객", "모바일 특화"] 
  },
  { 
    id: 6, 
    category: "시중 은행 상품 · 제 1금융권", 
    title: "급여하나 월복리 적금", 
    subtitle: "하나은행 · 1년~3년 만기 · 월 1만~300만원", 
    baseRate: "3.55", 
    maxRate: "5.0", 
    myRate: "4.1", 
    suitability: 78, 
    tags: ["적합도 78%", "급여 이체_우대", "카드 실적 우대"] 
  },
  { 
    id: 7, 
    category: "시중 은행 상품 · 제 1금융권", 
    title: "KB청년도약 특별적금", 
    subtitle: "KB국민은행 · 1년 만기 · 월 1만~50만원", 
    baseRate: "4.0", 
    maxRate: "5.5", 
    myRate: "4.8", 
    suitability: 65, 
    tags: ["적합도 65%", "자동이체 우대", "청청 응원"] 
  },
  { 
    id: 8, 
    category: "청약 상품", 
    title: "청년 주택드림 청약통장", 
    subtitle: "정부지원 (시중은행 위탁) · 무제한 · 월 2만~100만원", 
    baseRate: "2.0", 
    maxRate: "4.5", 
    myRate: "4.5", 
    suitability: 95, 
    tags: ["적합도 95%", "내집마련", "소득공제 혜택", "전환가입 가능"] 
  },
];

export default function ProductList() {
  const { accessToken } = useAuth();
  const isLoggedIn = !!accessToken; 

  const [searchTerm, setSearchTerm] = useState("");
  const [activeTab, setActiveTab] = useState("나에게 맞는 순");
  const [activeFilter, setActiveFilter] = useState("전체");
  const [showLoginModal, setShowLoginModal] = useState(false);

  const filters = [
    { label: "전체", icon: null },
    { label: "정부 지원", icon: icon_gov_support },
    { label: "제 1금융권", icon: icon_1_fin_sector },
    { label: "청약", icon: icon_subscription },
  ];

  const handleTabClick = (tabName) => {
    if (tabName === "내가 달성 가능한 금리 순" && !isLoggedIn) {
      setShowLoginModal(true);
      return;
    }
    setActiveTab(tabName);
  };

  const processedProducts = [...INITIAL_PRODUCTS]
    .filter(p => !searchTerm || p.title.includes(searchTerm))
    .sort((a, b) => activeTab === "나에게 맞는 순" ? b.suitability - a.suitability : parseFloat(b.myRate) - parseFloat(a.myRate));

  const topThree = processedProducts.slice(0, 3);

  const Sections = [
    { name: "정부 청년 상품", filterKey: "정부 지원" },
    { name: "시중 은행 상품 · 제 1금융권", filterKey: "제 1금융권" },
    { name: "청약 상품", filterKey: "청약" }
  ];

  return (
    <div className="h-screen w-screen bg-white flex flex-col overflow-hidden select-none font-[Inter]">
      
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
              className="w-full h-[70px] pl-16 pr-16 rounded-full border-2 border-[#03BFA5] text-[19px] text-[#A4BAB2] placeholder-gray-400 focus:outline-none shadow-sm bg-white"
            />
            <button className="absolute right-2.5 top-1/2 -translate-y-1/2 w-[38px] h-[38px] rounded-full bg-[#03BFA5] flex items-center justify-center hover:bg-[#02A68F] transition-colors">
              <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 12h14M12 5l7 7-7 7" /></svg>
            </button>
          </div>
        </div>
      </div>

      {/* 메인 콘텐츠 영역 바구니 */}
      <div className="max-w-370 w-full mx-60 fauex-1 pb-6 min-h-0 flex flex-col">
        
        <div className="flex items-end gap-4 h-auto shrink-0 relative z-50">
          
          {/* 탭 버튼 묶음 */}
          <div className="flex items-end mb-[-1px] relative z-10">
            <button
              onClick={() => handleTabClick("나에게 맞는 순")}
              className={`px-3 h-8 flex items-center justify-center text-[15px] rounded-tl-sm transition-all border-[2px] border-r-0 ${
                activeTab === "나에게 맞는 순"
                  ? "bg-[#03BFA5] text-white border-[#03BFA5]"
                  : "text-[#03BFA5] bg-white border-[#03BFA5]"
              }`}
            >
              나에게 맞는 순
            </button>
            <button
              onClick={() => handleTabClick("내가 달성 가능한 금리 순")}
              className={`px-3 h-8 flex items-center justify-center gap-1.5 text-[15px] rounded-tr-sm transition-all border-[2px] ${
                activeTab === "내가 달성 가능한 금리 순"
                  ? "bg-[#03BFA5] text-white border-[#03BFA5]"
                  : "text-[#03BFA5] bg-white border-[#03BFA5]"
              }`}
            >
              {!isLoggedIn && (
                <svg className="w-4 h-4 text-[#03BFA5]" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                  <rect x="5" y="11" width="14" height="10" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
              )}
              내가 달성 가능한 금리 순
            </button>
          </div>
          
          <div className="flex items-center gap-1 pb-1.5">
            <span className="text-[15px] text-[#03BFA5] tracking-tight">
              {activeTab === "나에게 맞는 순" ? "적합도란?" : "달성 가능 금리란?"}
            </span>
            
            <InfoIcon 
              text={
                activeTab === "나에게 맞는 순"
                  ? "선택하신 키워드(핵심 혜택 · 저축 기간 · 현재 신분 · 은행 거래)를 기준으로 산정한 매칭 점수예요.\n정부 지원 상품과 제 1금융권 상품별로 가중치를 다르게 반영하여 산정했어요."
                  : "입력하신 정보(소득 · 근속 · 주거래 은행 등)와 단계 1 키워드를 기반으로 실제로 받을 수 있는 우대조건만 적용한 실질 금리에요.\n정부 상품과 시중은행 상품의 계산 방식이 달라요."
              } 
            />
          </div>

        </div>

        <div className="bg-white rounded-b-xl border-[1.5px] border-[#EBEBEB] shadow-sm flex flex-col flex-1 min-h-0 overflow-hidden px-16 py-5">
          
          {/* 필터 버튼 바 */}
          <div className="p-6 pb-4 border-b border-gray-100 shrink-0 bg-white">
            <div className="flex items-center justify-between gap-2 flex-wrap">
              <div className="flex gap-2">
                {filters.map(filter => (
                  <button key={filter.label} onClick={() => setActiveFilter(filter.label)} className={`flex items-center gap-1.5 px-3 py-1 rounded-md border text-[14px] font-medium transition-colors ${activeFilter === filter.label ? "border-[2px] border-[#03BFA5] text-[#03BFA5] bg-[#F0FFFE] shadow-sm" : "border-[2px] border-[#E0DFDF] text-[#454545] bg-white hover:bg-gray-50"}`}>
                    {filter.icon && <img src={filter.icon} alt="" className="w-4 h-4" />}
                    {filter.label}
                  </button>
                ))}
              </div>
              <button className="flex items-center gap-1.5 px-4 py-1.5 rounded-sm border border-gray-200 bg-white text-[13px] text-[#454545] font-medium hover:bg-gray-50 shadow-sm whitespace-nowrap">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" /></svg>
                입력 정보 수정
              </button>
            </div>

            {/* 안내 문구 */}
            <div className="flex items-center gap-1.5 mt-4 text-[14px] font-medium text-[#03BFA5]">
              {isLoggedIn ? (
                <><div className="w-[16px] h-[16px] rounded-full bg-[#03BFA5] text-white flex items-center justify-center text-[11px] font-bold">✓</div><span>총 160개 상품 중 자격요건 미충족 상품 23개가 제외되었어요.</span></>
              ) : (
                <><div className="w-[16px] h-[16px] rounded-full bg-[#03BFA5] text-white flex items-center justify-center text-[12px] font-bold">i</div><span>로그인하면 자격요건 필터링 결과와 내가 달성 가능한 금리를 확인할 수 있어요.</span></>
              )}
            </div>
          </div>

          <div className="flex-1 overflow-y-auto p-6 pt-4 bg-white">
            
            {/* TOP 3 */}
            <div className="mb-6">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                {topThree.map((product, index) => (
                  <TopCard key={product.id} rank={index + 1} title={product.title} subtitle={product.subtitle} baseRate={product.baseRate} maxRate={product.maxRate} myRate={product.myRate} tags={product.tags} isBest={index === 0} isLoggedIn={isLoggedIn} />
                ))}
              </div>
            </div>

            {/* 하단 세로형 분리 목록 */}
            {Sections.map((sec) => {
              if (activeFilter !== "전체" && activeFilter !== sec.filterKey) return null;

              const sectionProducts = processedProducts.filter(p => p.category === sec.name);
              if (sectionProducts.length === 0) return null;

              return (
                <div key={sec.name} className="mt-8">
                  <h2 className="text-[16px] font-bold text-[#333333] mb-3 ml-1">
                    {sec.name}
                  </h2>
                  <div>
                    {sectionProducts.map((product) => (
                      <ListItem key={product.id} title={product.title} subtitle={product.subtitle} baseRate={product.baseRate} maxRate={product.maxRate} myRate={product.myRate} tags={product.tags} isLoggedIn={isLoggedIn} />
                    ))}
                  </div>
                </div>
              );
            })}

          </div>
        </div>
      </div>

      {/* 모달 팝업 */}
      {showLoginModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-[24px] w-full max-w-[420px] p-8 text-center shadow-2xl relative">
            <button onClick={() => setShowLoginModal(false)} className="absolute top-6 right-6 text-gray-400 hover:text-gray-600">
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
            <div className="w-14 h-14 bg-[#F0FFFE] rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-7 h-7 text-[#03BFA5]" fill="currentColor" viewBox="0 0 24 24"><path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/></svg>
            </div>
            <h2 className="text-[22px] font-bold text-[#03BFA5] mb-2">로그인 후 이용할 수 있어요</h2>
            <p className="text-[14px] text-[#03BFA5] mb-6 leading-relaxed">나에게 맞는 실질 우대금리를<br/>로그인 후 바로 확인해보세요.</p>
            <div className="flex flex-col gap-2.5 mb-8 text-left">
              {["소득·근속·주거래 은행 등 내 정보를 바탕으로 실질 금리를 계산해드려요.", "실제 충족 가능한 우대조건만 적용해 정확한 금리를 보여드려요.","단계2 정보 입력 후 더 정밀한 맞춤 추천이 활성화됩니다."].map((text, i) => (
                <div key={i} className="flex items-start gap-3 border border-[#03BFA5] rounded-lg p-3 bg-[#F0FFFE]">
                  <div className="mt-0.5 w-4 h-4 rounded-sm bg-[#03BFA5] flex items-center justify-center shrink-0">
                    <svg className="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" /></svg>
                  </div>
                  <span className="text-[12px] text-[#454545] leading-snug">{text}</span>
                </div>
              ))}
            </div>
            <div className="flex flex-col gap-2">
              <button className="w-full py-3 rounded-lg border border-gray-300 text-gray-700 font-bold text-[14px] hover:bg-gray-50">로그인하고 확인하기</button>
              <button className="w-full py-3 rounded-lg border border-gray-300 text-gray-700 font-bold text-[14px] hover:bg-gray-50">회원가입 후 이용하기</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}