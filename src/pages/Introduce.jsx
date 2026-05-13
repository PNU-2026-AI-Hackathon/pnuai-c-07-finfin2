import React from 'react';
import useScrollFadeIn from '../hooks/UseScrollFadeIn';
import usePageScale from '../hooks/UsePageScale';
import PhoneAnimation from '../components/phone_motion';
import leftphone from '../assets/phone_left.png'
import rightphone from '../assets/phone_right.png'
import centerphone from '../assets/phone_center.png'

export default function IntroducePage() {
  usePageScale(1400, 960);

  return (
    <div
      style={{ minWidth: '1400px' }}  // 이 이하로 절대 줄어들지 않음
    >
      <div style={{ zoom:'var(--page-scale, 1)' }}>
        <main className="flex-1">
          <IntroduceSection />
          <StepIntroSection />
          <MatchingScoreSection />
          <SummarySection />
          <ChatbotSection />
          <TopProductsSection />
          <MagazineSection />
          <MyPageSection />
        </main>
      </div>
    </div>
  );
}

function IntroduceSection() {
  const ref = useScrollFadeIn();

  return (
    <div ref={ref} className="w-full h-screen bg-linear-to-b from-[#EFFFFD] to-[#FFFFFF] flex flex-col items-center justify-center text-center px-6">
      <h1
        data-fade="title"
        className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-gmarket text-[45px] font-extrabold text-[#292929] mb-2 leading-tight tracking-tight"
      >
        아직도 아무 상품이나 가입하세요?
      </h1>
      <p
        data-fade="item"
        className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-gmarket text-[22px] font-medium text-[#03BFA5] leading-relaxed max-w-3xl"
      >
        30초 만에 원하는 금융상품을 찾는 청년 금융 소울메이트, y-Fin.
      </p>
      <div className="mt-10 w-full max-w-150">
        <PhoneAnimation />
      </div>
    </div>
  );
}

function StepIntroSection() {
  const ref = useScrollFadeIn();

  return (
    <section ref={ref} className="w-full h-screen bg-[#FFFFFF] flex justify-center items-center">
      <div className="text-center px-6">
        <h2
          data-fade="title"
          className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-gmarket text-[32px] font-extrabold text-[#454545] leading-tight tracking-tight"
        >
          단 <span className="text-[#03BFA5]">세 단계</span>로 나에게 맞는 금융상품 확인해요.
        </h2>
        <div data-fade="item" className="opacity-0 translate-y-6 transition-all duration-700 ease-out mt-4 w-20 h-1.5 bg-[#03BFA5]/20 mx-auto rounded-full" />
      </div>
    </section>
  );
}

function MatchingScoreSection() {
  const ref = useScrollFadeIn();

  return (
    <section ref={ref} className="py-24 bg-white flex flex-col items-center">
      <div
        data-fade="title"
        className="opacity-0 translate-y-6 transition-all duration-700 ease-out text-center mb-16 px-4"
      >
        <h2 className="font-gmarket text-[40px] font-extrabold text-[#454545] leading-tight">
          왜 이 상품이 나에게 최적인지, <br />
          <span className="text-[#03BFA5]">상품 조건</span>과 <span className="text-[#03BFA5]">수익률</span>을 통해 확인해보세요.
        </h2>
      </div>

      {/*카드 그리드*/}
      <div className="grid grid-cols-3 gap-8 max-w-7xl w-full px-6 items-stretch">
        {/* 사용자 프로필 */}
        <div
          data-fade="item"
          className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-inter bg-[#F9FAFB] rounded-[3rem] p-10 flex flex-col"
        >
          <h3 className="text-center text-lg font-bold text-[#555] mb-8">사용자 프로필</h3>
          <div className="space-y-4">
            <ProfileItem text="거주지 : 부산광역시 금정구" />
            <ProfileItem text="소득 : 연 2,800만원" />
            <ProfileItem text="직업 : IT 서비스 기획자" />
            <ProfileItem text="가족 : 1인 가구" />
            <ProfileItem text="주거래 은행 : BNK부산은행" />
          </div>
        </div>

        {/* 매칭점수 */}
        <div
          data-fade="item"
          className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-inter relative bg-white rounded-[3rem] p-12 flex flex-col items-center justify-center shadow-[0_20px_50px_rgba(226,226,226,0.7) scale-105 z-10"
        >
          <h3 className="text-lg font-bold text-[#454545] mb-8">매칭점수</h3>
          <div className="relative w-48 h-48 flex items-center justify-center">
            <svg className="w-full h-full -rotate-90">
              <circle cx="96" cy="96" r="88" stroke="#F1F5F9" strokeWidth="12" fill="transparent" />
              <circle cx="96" cy="96" r="88" stroke="#2DD4BF" strokeWidth="12" fill="transparent"
                      strokeDasharray={552} strokeDashoffset={552 * 0.2} strokeLinecap="round" />
            </svg>
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <span className="text-[40px] font-black text-[#454545]">80%</span>
              <span className="text-[13px] font-bold text-[#2DD4BF] mt-1 tracking-widest">HIGH MATCH</span>
            </div>
          </div>
          <p className="mt-10 text-[#454545] font-medium text-sm">
            총 5개의 핵심 항목 중 <span className="text-[#2DD4BF] font-bold">4개</span> 매칭 완료!
          </p>
        </div>

        {/* 추천상품 상세 */}
        <div
          data-fade="item"
          className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-inter bg-[#F8FAFB] rounded-[3rem] p-10 flex flex-col"
        >
          <h3 className="text-[17px] text-center font-bold text-[#454545] mb-8">
            추천상품 : 부산 청년 기쁨두배 통장 <span className="text-gray-400 font-normal text-xs ml-1">(최대 연 5.8%)</span>
          </h3>
          <div className="space-y-4 mb-10">
            <MatchBadge title="부산 지역 거주 및 재직(+1.0%)" sub="부산시 주소지 및 관내 기업 근로 상태 확인 완료" matched={true} />
            <MatchBadge title="청년 소득 기준 우대(+1.0%)" sub="기준 중위소득 140% 이하 조건 충족 (2,800만 원)" matched={true} />
            <MatchBadge title="온라인 금융 교육 이수 (+0.1%)" sub="가입 전 필수 온라인 교육 미이수 상태입니다." matched={false} />
          </div>
          <div className="mt-auto text-center border-t border-gray-200 pt-6">
            <span className="text-[13px] font-bold text-[#454545]">예상 최종 금리</span>
            <div className="text-[28px] font-bold text-[#2DD4BF] my-1">연 5.7%</div>
            <p className="text-[10px] text-gray-400">(교육 이수 시 최대 5.8% 적용 가능)</p>
          </div>
        </div>
      </div>
    </section>
  );
}

function ProfileItem({ text }) {
  return (
    <div className="bg-white px-6 py-5 rounded-[1.2rem] shadow-sm flex items-center gap-4 mb-4 transition-all hover:translate-x-1">
      <div className="text-[#03BFA5] shrink-0">
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="10" cy="10" r="9" stroke="currentColor" strokeWidth="1.5"/>
          <path d="M6 10L9 13L14 8" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
      </div>
      <span className="text-[17px] font-bold text-[#454545] tracking-tight">{text}</span>
    </div>
  );
}

function MatchBadge({ title, sub, matched }) {
  return (
    <div className="px-5 py-4 rounded-2xl bg-white shadow-sm">
      <div className="flex justify-between items-center mb-1">
        <h4 className="text-[18px] font-semibold text-[#454545]">{title}</h4>
        <span className={`text-[18px] font-semibold ${matched ? 'text-[#2DD4BF]' : 'text-[#B4B4B4]'}`}>
          {matched ? '매칭!' : '미매칭!'}
        </span>
      </div>
      <p className="text-[11px] text-gray-400">{sub}</p>
    </div>
  );
}

/*3줄 요약*/
function SummarySection() {
  const ref = useScrollFadeIn();

  return (
    <section ref={ref} className="py-24 bg-white flex flex-col items-center">
      <div
        data-fade="title"
        className="opacity-0 translate-y-6 transition-all duration-700 ease-out text-center mb-16"
      >
        <h2 className="font-gmarket text-[32px] font-extrabold text-[#333] leading-tight mb-4">
          복잡한 약관은 이제 끝! <br />
          y-Fin.이 <span className="text-[#2DD4BF]">핵심만 3줄로</span> 요약해드려요.
        </h2>
      </div>

      {/*메인 요약 카드 박스*/}
      <div
        data-fade="item"
        className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-inter max-w-5xl w-full px-6"
      >
        <div className="border-3 border-[#03BFA5] rounded-[2.6rem] p-1 overflow-hidden shadow-sm">
          <div className="bg-[#EFFFFD] rounded-[2.2rem] overflow-hidden">
            <div className="px-10 py-8 border-b border-gray-100 flex justify-between items-end">
              <div>
                <span className="text-[#03BFA5] font-bold text-sm mb-2 block tracking-tight">y-Fin 핵심 요약</span>
                <h3 className="text-[28px] font-black text-[#333]">청년 미래 적금</h3>
              </div>
              <div className="text-right pb-1">
                <span className="text-xl font-bold text-[#454545]">최대 연 </span>
                <span className="text-[32px] font-black text-[#03BFA5]">16.9%</span>
                <span className="text-xl font-bold text-[#454545]"> 수익 효과</span>
              </div>
            </div>
            {/*카드 본문 (3줄 요약 리스트)*/}
            <div className="px-10 py-10 space-y-8 bg-[#FFFFFF]">
              <SummaryRow label="가입 대상" content="만 19~34세 청년 중 개인소득 6,000만 원 이하 및 가구소득 중위 200% 이하" />
              <SummaryRow label="핵심 혜택" content="3년 만기 시 정부 기여금(6~12%)과 이자 비과세 혜택을 더해 최대 약 2,200만 원의 목돈 수령" />
              <SummaryRow label="납입 방식" content="월 최대 50만 원까지 저축하며, 기존 5년 만기의 부담을 3년으로 줄인 단기 압축형 자산 형성" />
            </div>

            {/* 카드 하단 태그 영역 */}
            <div className="px-10 py-8 bg-[#F7F6F3] border-t border-gray-100 flex gap-3 flex-wrap">
              <Tag text="매칭 87%" icon="checked" />
              <Tag text="상품 기간: 156주" />
              <Tag text="은행 앱(비대면), 영업점 방문(대면) 신청 가능" />
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

function SummaryRow({ label, content }) {
  return (
    <div className="flex items-start gap-5">
      <div className="w-2.5 h-2.5 bg-[#03BFA5] rounded-full mt-2.5 shrink-0 shadow-[0_0_8px_rgba(3,191,165,0.4)]" />
      <div className="flex flex-row items-center gap-2">
        <span className="text-[19px] font-black text-[#454545] whitespace-nowrap">{label} :</span>
        <p className="text-[17px] font-medium text-[#666] leading-relaxed">{content}</p>
      </div>
    </div>
  );
}

function Tag({ text, icon }) {
  return (
    <div className={`px-5 py-2.5 rounded-full border border-gray-200 bg-white shadow-sm flex items-center gap-2
      ${icon ? 'text-[#03BFA5] font-bold border-[#03BFA5]/20' : 'text-gray-500 font-medium'}`}>
      {icon && (
        <svg width="18" height="18" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="10" cy="10" r="9" stroke="currentColor" strokeWidth="1.5"/>
          <path d="M6 10L9 13L14 8" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
      )}
      <span className="text-[15px]">{text}</span>
    </div>
  );
}

function ChatbotSection() {
  const ref = useScrollFadeIn();

  return (
    <section ref={ref} className="w-full h-screen bg-[#FFFFFF] flex justify-center items-center">
      <div className="text-center px-6">
        <h2
          data-fade="title"
          className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-gmarket text-[32px] font-extrabold text-[#454545] leading-tight tracking-tight"
        >
          어려운 금융 용어는<br /><span className="text-[#03BFA5]">챗봇 AI한테</span> 바로바로 물어보세요.
        </h2>
      </div>
    </section>
  );
}

function TopProductsSection() {
  const ref = useScrollFadeIn();

  const products = [
    {
      agency: "국토교통부",
      badges: ["BEST", "비과세(조건)"],
      subTitle: "내 집 마련 필수템",
      title: "청년 주택드림 청약통장",
      desc: "자유 적립식 · 청약 당첨 시까지 · 월 최대 100만원",
      rate: "4.5%",
      subDesc: "청년 우대 1.7%p 포함\n(연 2.2%대 저금리 대출 연계 혜택)",
      isHighlighted: true,
      buttonText: "바로 가입하기"
    },
    {
      agency: "보건복지부",
      badges: ["비과세", "근로 청년"],
      subTitle: "일하는 청년들을 위한 보너스 계좌",
      title: "청년 내일 저축 계좌",
      desc: "정액 적립식 · 36개월 · 월 10만원(최대 50만원)",
      rate: "5.0%",
      subDesc: "은행별 최고 우대금리 포함\n(정부 매칭 지원금 월 최대 30만원 별도 지급)",
      isHighlighted: false,
      buttonText: "자세히 보기"
    },
    {
      agency: "국방부 · 병무청",
      badges: ["비과세", "군인 전용"],
      subTitle: "군인들을 위한 압도적 수익률",
      title: "장병 내일 준비 적금",
      desc: "자유 적립식 · 15~24개월 · 월 최대 55만원",
      rate: "5.0%",
      subDesc: "15개월 이상 가입 시 은행 우대금리 포함\n(원리금 100% 정부 매칭 지원)",
      isHighlighted: false,
      buttonText: "자세히 보기"
    }
  ];

  return (
    <section ref={ref} className="py-24 bg-white flex flex-col items-center px-6">
      <div
        data-fade="title"
        className="opacity-0 translate-y-6 transition-all duration-700 ease-out text-center mb-20"
      >
        <h2 className="font-gmarket text-[32px] font-extrabold text-[#333] leading-tight">
          혼자 고민하지 마세요. <br />
          또래들이 가장 많이 가입한 상품 <span className="text-[#2DD4BF]">TOP 3</span>
        </h2>
      </div>

      {/* 카드 그리드 */}
      <div className="grid grid-cols-3 gap-8 max-w-7xl w-full">
        {products.map((product, index) => (
          <div
            key={index}
            data-fade="item"
            className="opacity-0 translate-y-6 transition-all duration-700 ease-out"
          >
            <ProductCard {...product} />
          </div>
        ))}
      </div>
    </section>
  );
}

function ProductCard({ agency, badges, subTitle, title, desc, rate, subDesc, isHighlighted, buttonText }) {
  return (
    <div className={`font-inter p-10 rounded-[2.5rem] border-2 flex flex-col h-full
      ${isHighlighted ? 'border-[#2DD4BF] bg-[#EFFFFD]' : 'border-[#E2E8F0] bg-[#FFFFFF] shadow-sm'}`}>
      {/*기관 및 뱃지 */}
      <div className="flex justify-between items-center mb-10">
        <span className="text-[15px] text-[#454545]">{agency}</span>
        <div className="flex gap-2">
          {badges.map((badge, i) => (
            <span key={i} className="px-3 py-1 bg-[#2DD4BF] text-white text-[12px] font-bold rounded-full">{badge}</span>
          ))}
        </div>
      </div>
      {/* 상품 정보 */}
      <div className="mb-8">
        <p className="text-[#2DD4BF] font-bold text-[14px] mb-2">{subTitle}</p>
        <h3 className="text-[25px] font-bold text-[#454545] mb-3 leading-tight">{title}</h3>
        <p className="text-[13px] text-[#454545] font-medium">{desc}</p>
      </div>

      {/* 금리 정보 */}
      <div className="mb-10 flex-1">
        <p className="text-[14px] font-bold text-[#2DD4BF]">최대 연</p>
        <div className="text-[47px] font-bold text-[#2DD4BF] leading-none my-2">{rate}</div>
        <p className="text-[13px] text-gray-500 font-semibold leading-relaxed whitespace-pre-wrap">{subDesc}</p>
      </div>

      {/* 하단 버튼/링크 */}
      <div className="pt-8 border-t border-gray-100 flex justify-between items-center cursor-pointer">
        <span className="text-[16px] font-bold text-[#454545]">{buttonText}</span>
        <svg className={`w-8 h-8 ${isHighlighted ? 'text-[#2DD4BF]' : 'text-gray-300'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
        </svg>
      </div>
    </div>
  );
}

/* 금융지식 매거진 섹션*/
function MagazineSection() {
  const ref = useScrollFadeIn();

  const articles = [
    { title: "복리와 단리의 차이,\n내 돈이 불어나는 진짜 원리" },
    { title: "신용점수 관리, 사회초년생이\n꼭 알아야 할 3가지 수칙" },
    { title: "ISA? IRP? 세금을 아끼는\n'절세 통장' 완벽 정리" },
    { title: "2026년 하반기 금리 전망,\n예·적금 갈아타기 타이밍은?" },
    { title: "6월 출시 '청년미래적금', 기존\n도약계좌 가입자도 갈아탈 수 있을까?" },
    { title: "디지털 자산 1억 시대,\n핀테크가 바꾸는 미래 결제 트렌드" }
  ];

  return (
    <section ref={ref} className="py-24 bg-white flex flex-col items-center px-6">
      {/* 섹션 타이틀 */}
      <div
        data-fade="title"
        className="opacity-0 translate-y-6 transition-all duration-700 ease-out text-center mb-16"
      >
        <h2 className="font-gmarket text-[32px] font-extrabold text-[#333] leading-tight">
          어려운 <span className="text-[#03BFA5]">금융 지식,</span> <br />
          y-Fin.으로 가볍게 쌓아보세요.
        </h2>
      </div>

      {/* 매거진 카드 그리드 */}
      <div className="grid grid-cols-3 gap-x-8 gap-y-12 max-w-7xl w-full">
        {articles.map((article, index) => (
          <div
            key={index}
            data-fade="item"
            className="opacity-0 translate-y-6 transition-all duration-700 ease-out"
          >
            <MagazineCard {...article} />
          </div>
        ))}
      </div>
    </section>
  );
}

function MagazineCard({ title, imgUrl }) {
  return (
    <div className="font-inter overflow-hidden rounded-4xl border-[1.5px] border-[#B6D6D2] shadow-sm">
      <div className="aspect-video bg-gray-100 overflow-hidden">
        <img src={imgUrl} alt="image" className="w-full h-full object-cover" />
      </div>

      {/* 하단 텍스트 영역 */}
      <div className="bg-white p-8 border-t border-[#B6D6D2]/10">
        <h3 className="text-[19px] font-semibold text-[#454545] leading-snug whitespace-pre-wrap">{title}</h3>
      </div>
    </div>
  );
}

/*마이페이지 섹션*/
function MyPageSection() {
  const ref = useScrollFadeIn();

  return (
    <section ref={ref} className="w-full h-screen bg-linear-to-b from-[#FFFFFF] to-[#EFFFFD] flex justify-center items-center">
      <div className="text-center px-6">
        <h2
          data-fade="title"
          className="opacity-0 translate-y-6 transition-all duration-700 ease-out font-gmarket text-[32px] font-extrabold text-[#454545] leading-tight tracking-tight"
        >
          당신이 입력한 정보,<br /><span className="text-[#03BFA5]">마이페이지</span>에 저장하고 다음 추천에 활용해보세요.
        </h2>
      </div>
    </section>
  );
}