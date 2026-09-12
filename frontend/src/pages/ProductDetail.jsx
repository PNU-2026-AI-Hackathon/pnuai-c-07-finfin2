import { useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import client, { withAuth } from "../api/client";
import {
  findProductViewById,
  getActiveRecommendationResult,
} from "../utils/recommendationResult";
import { getProductApplicationBadge, getProductApplicationBadgeVariant, getProductApplyUrl, openProductApplication } from "../utils/productApplyLink";
import applicationFallbackSearchIcon from "../assets/application-fallback/search.svg";
import applicationFallbackInstitutionIcon from "../assets/application-fallback/institution.svg";
import applicationFallbackCopyIcon from "../assets/application-fallback/copy.svg";
import applicationFallbackLocationIcon from "../assets/application-fallback/location.svg";

function ArrowLeftIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19 8 12l7-7" />
    </svg>
  );
}

function BuildingIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 21h18M5 21V7l7-4 7 4v14M9 21v-6h6v6M8 10h.01M12 10h.01M16 10h.01" />
    </svg>
  );
}

function ClockIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="12" r="8.5" strokeWidth={2} />
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 7v5l3 2" />
    </svg>
  );
}

function WonIcon({ className = "" }) {
  return <span className={`font-semibold leading-none ${className}`}>￦</span>;
}

function InfoCircleIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="12" r="9" strokeWidth={2} />
      <path strokeLinecap="round" strokeWidth={2} d="M12 10.5v5" />
      <circle cx="12" cy="7.5" r="1" fill="currentColor" stroke="none" />
    </svg>
  );
}

function StarIcon({ className = "" }) {
  return (
    <svg className={className} fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path d="m12 2.7 2.8 5.68 6.27.91-4.54 4.42 1.07 6.24L12 17l-5.6 2.95 1.07-6.24-4.54-4.42 6.27-.91L12 2.7Z" />
    </svg>
  );
}

function ExternalLinkIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.2} d="M14 5h5v5M10 14 19 5M19 14v5H5V5h5" />
    </svg>
  );
}

function HeartIcon({ className = "" }) {
  return (
    <svg className={className} fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path d="M12 20.3 10.6 19C5.6 14.5 2.3 11.5 2.3 7.8A5.28 5.28 0 0 1 7.6 2.5c2 0 3.9.94 5.1 2.42A6.36 6.36 0 0 1 17.8 2.5a5.28 5.28 0 0 1 5.3 5.3c0 3.7-3.3 6.7-8.3 11.25l-1.4 1.25a2.06 2.06 0 0 1-1.4 0Z" />
    </svg>
  );
}

function CalendarIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 3v3M17 3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1Z" />
    </svg>
  );
}

function CalculatorIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 7h6M8 11h.01M12 11h.01M16 11h.01M8 15h.01M12 15h.01M16 15h.01M6 3h12a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z" />
    </svg>
  );
}

function DetailBadge({ label }) {
  const isSuitability = label.includes("적합도");
  const isMint = label.includes("_ok") || label.includes("정부기여금") || label.includes("비과세 혜택");

  return (
    <span
      className={`flex h-[33px] items-center justify-center rounded-[6px] px-[13px] text-[16px] font-normal leading-[1.2] ${
        isSuitability
          ? "bg-[#FFF4E0] text-[#E65200]"
          : isMint
            ? "bg-[#E0FDF9] text-[#333333]"
            : "bg-[#F2F3F5] text-[#333333]"
      }`}
    >
      {label}
    </span>
  );
}

function SectionTitle({ icon, children, className = "" }) {
  return (
    <div className={`flex h-[26px] items-center gap-[10px] text-[#454545] ${className}`}>
      {icon}
      <h2 className="text-[24px] font-medium leading-[1.2]">{children}</h2>
    </div>
  );
}

function ProductInfoCard({ product, cardRef }) {
  const rows = [
    { label: "상품 유형", value: product.productType },
    { label: "저축 기간", value: product.savingPeriod },
    { label: "납입 가능 금액", value: product.depositRange },
    { label: "가입 방법", value: product.applicationMethod },
    { label: "가입 대상", value: product.target, highlight: true },
    { label: "유의 사항", value: product.caution },
  ];

  return (
    <div ref={cardRef} className="rounded-[10px] border border-[#D5D5D5] px-6 py-8 lg:min-h-[470px] lg:px-[34px] lg:py-[42px]">
      <div className="flex w-full max-w-[736px] flex-col">
        {rows.map((row, index) => (
          <div
            key={row.label}
            className={`grid grid-cols-1 gap-2 py-[15px] text-[18px] leading-[1.3] lg:grid-cols-[108px_minmax(0,1fr)] lg:gap-[52px] ${
              index === 0 ? "pt-0" : "border-t border-[#E5E5E5]"
            } ${index === rows.length - 1 ? "pb-0" : ""}`}
          >
            <dt className="font-normal text-[#6B6B6B]">{row.label}</dt>
            <dd className={`text-[19px] font-medium ${row.highlight ? "text-[#03BFA5]" : "text-[#454545]"}`}>
              {Array.isArray(row.value)
                ? row.value.map((line) => <p key={line}>{line}</p>)
                : row.value}
            </dd>
          </div>
        ))}
      </div>
    </div>
  );
}

function RateTable({ product }) {
  return (
    <div className="w-full max-w-[736px] text-[18px] font-medium leading-[1.2]">
      <div className="grid h-[50px] grid-cols-3 items-center rounded-t-[10px] border border-b-0 border-[#03BFA5] bg-[#F7FFFE] px-[22px] text-[#6B6B6B]">
        <span>기간</span>
        <span className="text-center">기본금리</span>
        <span className="text-right">최고금리</span>
      </div>
      <div className="grid h-[50px] grid-cols-3 items-center rounded-b-[10px] border border-[#03BFA5] px-[22px] text-[#454545]">
        <span>{product.rateTerm}</span>
        <span className="text-center">{product.baseRateDisplay}</span>
        <span className="text-right">{product.maxRateDisplay}</span>
      </div>
    </div>
  );
}

function ConditionIcon({ status }) {
  if (status === "neutral") return null;

  const isMatched = status === "matched";

  return (
    <span className={`flex size-[28px] shrink-0 items-center justify-center rounded-full ${isMatched ? "bg-[#03BFA5]" : "bg-[#D3455B]"}`}>
      {isMatched ? (
        <svg className="size-[18px] text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="m6 12 4 4 8-8" />
        </svg>
      ) : (
        <svg className="size-[16px] text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M6 6l12 12M18 6 6 18" />
        </svg>
      )}
    </span>
  );
}

function RateConditionRow({ item }) {
  const statusClass = {
    matched: "border-[#03BFA5] bg-[#F7FFFE]",
    missed: "border-[#D3455B] bg-[#FCF4F5]",
    neutral: "border-[#C6D3D3] bg-white",
  }[item.status];

  const valueColor = item.status === "matched" ? "text-[#03BFA5]" : item.status === "missed" ? "text-[#D3455B]" : "text-[#454545]";

  return (
    <div className={`flex min-h-[65px] items-center rounded-[10px] border px-[20px] py-[14px] ${statusClass}`}>
      <div className="flex w-full items-center gap-[20px]">
        <ConditionIcon status={item.status} />
        <div className={`flex flex-1 items-center justify-between gap-[12px] text-[18px] leading-[1.3] ${item.status === "neutral" ? "pl-[10px]" : ""}`}>
          <span className="font-medium text-[#454545]">{item.label}</span>
          <span className={`font-semibold ${valueColor}`}>{item.value}</span>
        </div>
      </div>
    </div>
  );
}

function RateInfoCard({ product }) {
  return (
    <div className="rounded-[10px] border border-[#D5D5D5] px-6 py-8 lg:min-h-[530px] lg:px-[34px] lg:py-[30px]">
      <div className="flex w-full max-w-[736px] flex-col gap-[24px]">
        <RateTable product={product} />
        <div className="flex w-full max-w-[710px] flex-col gap-[16px]">
          <SectionTitle icon={<StarIcon className="size-[23px] text-[#03BFA5]" />}>우대금리 조건</SectionTitle>
          <div className="flex w-full max-w-[736px] flex-col gap-[7px]">
            {product.rateConditions.map((item) => (
              <RateConditionRow key={`${item.label}-${item.value}`} item={item} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

function NoticeBox({ children }) {
  return (
    <div className="flex min-h-[72px] w-full max-w-[700px] items-center justify-center rounded-[10px] border border-[#03BFA5] bg-[#F7FFFE] px-[24px] py-[16px] text-[#454545]">
      <div className="flex max-w-[645px] items-start gap-[11px]">
        <InfoCircleIcon className="mt-[1px] size-[20px] shrink-0 text-[#03BFA5]" />
        <p className="text-[18px] font-normal leading-[1.3]">{children}</p>
      </div>
    </div>
  );
}

function BankRateSummary({ product, isLoggedIn }) {
  const baseRate = product.baseRateDisplay || `${product.baseRate}%`;
  const maxRate = product.maxRateDisplay || `${product.maxRate}%`;

  return (
    <div className="flex h-[238px] w-full max-w-[620px] flex-col items-center justify-center rounded-[10px] border border-[#D5D5D5] px-6 py-8 lg:px-[28px] lg:py-[32px]">
      <div className="grid w-full grid-cols-1 items-center justify-items-center gap-7 md:grid-cols-[1fr_1px_1fr] md:gap-0">
        <div className="w-fit max-w-full text-left">
          <p className="whitespace-nowrap text-[21px] font-medium leading-[1.2] text-[#454545]">기본 금리</p>
          <p className="mt-[8px] whitespace-nowrap text-[42px] font-bold leading-[1.2] text-[#454545]">{baseRate}</p>
        </div>
        <div className="hidden h-[121px] w-px bg-[#D5D5D5] md:block" />
        <div className="w-fit max-w-full text-left">
          <p className="whitespace-nowrap text-[21px] font-medium leading-[1.2] text-[#454545]">최고 금리</p>
          <p className="mt-[8px] whitespace-nowrap text-[42px] font-bold leading-[1.2] text-[#03BFA5]">{maxRate}</p>
        </div>
      </div>
      <div className="mt-[16px] flex h-[44px] w-full max-w-[461px] items-center justify-center gap-[22px] rounded-full border border-[#03BFA5] bg-[#EFFFFD] px-[26px] leading-[1.2] text-[#03BFA5]">
        <span className="whitespace-nowrap text-center text-[16px] font-normal">내가 받을 수 있는 금리</span>
        <span className="whitespace-nowrap text-center text-[24.37px] font-semibold">연 {isLoggedIn ? product.myRate : "???"} %</span>
      </div>
    </div>
  );
}

function ContributionSummary({ product }) {
  return (
    <div className="flex min-h-[242px] w-full max-w-[699px] flex-col items-center justify-center rounded-[10px] border border-[#D5D5D5] px-6 py-8 lg:px-[32px] lg:py-[36px]">
      <div className="grid w-full grid-cols-1 items-center justify-items-center gap-7 md:grid-cols-[1fr_1px_1fr] md:gap-0">
        <div className="w-fit max-w-full text-left">
          <p className="whitespace-nowrap text-[24.7px] font-medium leading-[1.2] text-[#454545]">기여금 환산 수익률</p>
          <p className="mt-[11px] whitespace-nowrap text-[49.4px] font-bold leading-[1.2] text-[#454545]">{product.contributionRate}</p>
        </div>
        <div className="hidden h-[108px] w-px bg-[#D5D5D5] md:block" />
        <div className="w-fit max-w-full text-left">
          <p className="whitespace-nowrap text-[24.7px] font-medium leading-[1.2] text-[#454545]">예상 만기 기여금 총액</p>
          <p className="mt-[11px] whitespace-nowrap text-[49.4px] font-bold leading-[1.2] text-[#03BFA5]">{product.maturityContribution}</p>
        </div>
      </div>
      <p className="mt-[22px] w-full text-center text-[23.1px] font-medium leading-[1.2] text-[#606060]">{product.contributionCaption}</p>
    </div>
  );
}

function ProductSummary({ product, isBankProduct, isLoggedIn }) {
  return isBankProduct ? <BankRateSummary product={product} isLoggedIn={isLoggedIn} /> : <ContributionSummary product={product} />;
}

function ApplicationFallbackModal({ product, onClose, onOpenInstitutionPage }) {
  const [isCopied, setIsCopied] = useState(false);
  const isGovernmentProduct = getProductApplicationBadgeVariant(product) === "government";
  const channelLabel = isGovernmentProduct ? "복지로에서 검색하기" : `${product.institution} 홈페이지에서 검색하기`;
  const institutionDescription = isGovernmentProduct
    ? "담당 기관 ∙ 읍∙면∙동 행정복지센터 / 복지로"
    : `담당 기관 ∙ ${product.institution}`;

  const handleCopy = async () => {
    try {
      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(product.title);
      } else {
        const textArea = document.createElement("textarea");
        textArea.value = product.title;
        textArea.style.position = "fixed";
        textArea.style.opacity = "0";
        document.body.appendChild(textArea);
        textArea.select();
        const copied = document.execCommand("copy");
        document.body.removeChild(textArea);
        if (!copied) throw new Error("Clipboard copy failed");
      }
      setIsCopied(true);
    } catch {
      setIsCopied(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black/40 px-4 py-8" role="presentation" onMouseDown={onClose}>
      <section
        role="dialog"
        aria-modal="true"
        aria-labelledby="application-fallback-title"
        className="w-full max-w-[587px] rounded-[28px] bg-white px-[46px] pb-[42px] pt-[70px] text-center shadow-2xl"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <img src={applicationFallbackInstitutionIcon} alt="" className="mx-auto size-[60px]" />
        <h2 id="application-fallback-title" className="mt-5 text-[26px] font-semibold leading-[1.22] text-[#373737]">기관 공식 채널에서 신청해 주세요</h2>
        <p className="mt-4 text-[18px] leading-[1.44] text-[#6B7571]">직접 연결 링크가 확인되지 않아<br />담당 기관 안내로 대체해 드려요.</p>

        <div className="mt-10 rounded-[10px] bg-[#F0FFFE] px-[22px] py-[30px] text-left">
          <div className="flex items-end justify-between gap-4 border-b border-[#D0DDDC] pb-[23px]">
            <div>
              <p className="text-[17px] font-medium text-[#6F7975]">상품명</p>
              <p className="mt-[10px] text-[22px] font-semibold leading-[1.22] text-[#373737]">{product.title}</p>
            </div>
            <button type="button" onClick={handleCopy} className="flex h-[39px] shrink-0 cursor-pointer items-center gap-1 rounded-[9px] border border-[#D0DDDC] bg-white px-[11px] text-[18px] font-medium text-[#03BFA5]">
              {isCopied ? (
                <svg viewBox="0 0 24 24" aria-hidden="true" className="size-[22px]" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="m5 12 4.2 4.2L19 6.5" />
                </svg>
              ) : <img src={applicationFallbackCopyIcon} alt="" className="size-[22px]" />}
              {isCopied ? "완료" : "복사"}
            </button>
          </div>
          <div className="mt-[23px] flex items-start gap-[5px] text-[18px] font-medium leading-[1.22] text-[#6F7975]">
            <img src={applicationFallbackLocationIcon} alt="" className="size-[22px] shrink-0" />
            <span>{institutionDescription}</span>
          </div>
        </div>

        <button type="button" onClick={onOpenInstitutionPage} className="mt-5 flex h-[64px] w-full cursor-pointer items-center justify-center gap-5 rounded-[10px] bg-[#03BFA5] text-[22px] font-semibold text-white">
          <img src={applicationFallbackSearchIcon} alt="" className="size-[26px]" />
          {channelLabel}
        </button>
        <button type="button" onClick={onClose} className="mt-[18px] cursor-pointer text-[20px] font-medium text-[#454545]/70">닫기</button>
        <div className="mt-[36px] border-t border-[#D9D9D9] pt-[21px] text-[17px] leading-[1.3] text-[#9A9A9A]">
          <p>Y-Fin은 해당 상품의 판매·중개 주체가 아니며,</p>
          <p>신청은 기관 공식 페이지에서 진행됩니다.</p>
        </div>
      </section>
    </div>
  );
}

function LoginRequiredModal({ onClose, onLogin }) {
  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black/40 px-4 py-8" role="presentation" onMouseDown={onClose}>
      <section
        role="dialog"
        aria-modal="true"
        aria-labelledby="login-required-title"
        className="w-full max-w-[420px] rounded-[20px] bg-white px-8 py-10 text-center shadow-2xl"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="mx-auto flex size-14 items-center justify-center rounded-full bg-[#EFFFFD] text-[#03BFA5]">
          <HeartIcon className="size-7" />
        </div>
        <h2 id="login-required-title" className="mt-5 text-[22px] font-semibold text-[#454545]">로그인 후 찜할 수 있어요</h2>
        <p className="mt-3 text-[16px] leading-[1.5] text-[#6B7571]">로그인하면 관심 있는 상품을<br />마이페이지에 저장할 수 있어요.</p>
        <div className="mt-7 grid grid-cols-2 gap-3">
          <button type="button" onClick={onClose} className="h-12 cursor-pointer rounded-[10px] border border-[#D5D5D5] text-[16px] font-semibold text-[#454545]">닫기</button>
          <button type="button" onClick={onLogin} className="h-12 cursor-pointer rounded-[10px] bg-[#03BFA5] text-[16px] font-semibold text-white">로그인</button>
        </div>
      </section>
    </div>
  );
}

function RightPanel({ product, onEditRate, onApply, onFavorite, isLoggedIn, isFavorite, topOffset }) {
  const applicationBadgeVariant = getProductApplicationBadgeVariant(product);
  const isBankProduct = applicationBadgeVariant === "bank";
  const applicationBadgeClass = applicationBadgeVariant === "bank"
    ? "bg-[#F4F5F6] text-[#454545]"
    : "bg-[#F7FFFE] text-[#03BFA5]";

  return (
    <aside
      className="relative flex w-full max-w-[620px] flex-col min-[1100px]:pt-[var(--right-panel-offset,222px)]"
      style={topOffset ? { "--right-panel-offset": `${topOffset}px` } : undefined}
    >
      <ProductSummary product={product} isBankProduct={isBankProduct} isLoggedIn={isLoggedIn} />

      <div className={`${isBankProduct ? "mt-[30px]" : "mt-[21px]"} flex flex-col`}>
        <div className="grid grid-cols-[1fr_82px] gap-[10px]">
          <button
            type="button"
            onClick={onApply}
            className="flex h-[64px] cursor-pointer items-center justify-center gap-[14px] rounded-[10px] border border-[#03BFA5] bg-[#03BFA5] text-[22px] font-medium leading-[1.2] text-white transition-colors hover:bg-[#02A892]"
          >
            <ExternalLinkIcon className="size-[30px]" />
            신청하러 가기
          </button>
          <button
            type="button"
            onClick={onFavorite}
            aria-label="관심 상품"
            aria-pressed={isFavorite}
            className={`flex h-[64px] cursor-pointer items-center justify-center rounded-[10px] border-2 transition-colors ${
              isFavorite
                ? "border-[#03BFA5] bg-[#EFFFFD] text-[#03BFA5]"
                : "border-[#D4D4D4] text-[#D4D4D4] hover:border-[#03BFA5] hover:text-[#03BFA5]"
            }`}
          >
            <HeartIcon className="size-[30px]" />
          </button>
        </div>
        {isBankProduct && product.calculator && (
          <button
            type="button"
            onClick={onEditRate}
            className="mt-[10px] flex h-[64px] w-full cursor-pointer items-center justify-center gap-[12px] rounded-[10px] border-2 border-[#03BFA5] bg-white text-[22px] font-medium leading-[1.2] text-[#03BFA5] transition-colors hover:bg-[#F7FFFE]"
          >
            <CalculatorIcon className="size-[30px]" />
            수익률 계산기
          </button>
        )}

        {!isBankProduct && (
          <div className="mt-[32px] flex flex-col">
            <div className="flex items-center gap-[10px] text-black">
              <CalendarIcon className="size-[30px]" />
              <h2 className="text-[26px] font-medium leading-[1.2]">모집 기간</h2>
            </div>
            <div className="mt-[18px] flex h-[80px] items-center justify-center rounded-[10px] border-2 border-[#03BFA5] bg-[#F7FFFE] px-[34px] text-center text-[30px] font-medium leading-[1.2] text-[#03BFA5]">
              {product.recruitPeriod}
            </div>
          </div>
        )}

        <div className={`${isBankProduct ? "mt-[20px] h-[48px]" : "mt-[24px] h-[48px]"} flex w-fit items-center justify-center rounded-[10px] px-[24px] text-[20px] font-semibold leading-[1.2] ${applicationBadgeClass}`}>
          {getProductApplicationBadge(product)}
        </div>
        <div className="mt-[24px] h-px w-full bg-[#D5D5D5]" />
        <p className="mt-[20px] text-[18px] font-normal leading-[1.4] text-[#9C9C9C]">
          Y-Fin은 해당 상품의 판매 ∙ 중개 주체가 아니며,<br />
          신청은 기관 공식 페이지에서 진행됩니다.
        </p>
      </div>
    </aside>
  );
}

function ProductHeader({ product }) {
  return (
    <div className="flex flex-col gap-[30px]">
      <div className="flex flex-wrap gap-[9px]">
        {product.tags.map((tag) => (
          <DetailBadge key={tag} label={tag} />
        ))}
      </div>

      <div className="flex flex-col gap-[15px]">
        <h1 className="text-[34px] font-bold leading-[1.2] text-[#373737]">{product.detailTitle}</h1>
        <div className="flex flex-wrap items-center gap-[14px] text-[19px] font-medium leading-[1.2] text-[#454545]">
          <span className="flex items-center gap-[5px]">
            <BuildingIcon className="h-[18px] w-[16px]" />
            {product.metaInstitution}
          </span>
          <span className="flex items-center gap-[5px]">
            <ClockIcon className="size-[22px]" />
            기간 {product.periodSummary}
          </span>
          <span className="flex items-center gap-[5px]">
            <WonIcon />
            {product.amountSummary}
          </span>
        </div>
      </div>
    </div>
  );
}

function LeftPanel({ product, onBack, panelRef, productInfoRef }) {
  return (
    <div ref={panelRef} className="flex w-full max-w-[700px] flex-col gap-[18px]">
      <button
        type="button"
        onClick={onBack}
        className="flex w-fit cursor-pointer items-center gap-[8px] text-[20px] font-medium leading-[1.2] text-[#454545] transition-colors hover:text-[#03BFA5]"
      >
        <ArrowLeftIcon className="size-[24px]" />
        상품 리스트로 돌아가기
      </button>

      <div className="flex flex-col gap-[30px]">
        <ProductHeader product={product} />

        <section className="flex flex-col gap-[20px]">
          <SectionTitle icon={<InfoCircleIcon className="size-[22px] text-[#03BFA5]" />}>상품 안내</SectionTitle>
          <ProductInfoCard product={product} cardRef={productInfoRef} />
        </section>

        <section className="flex flex-col gap-[10px]">
          <div className="flex flex-col gap-[20px]">
            <SectionTitle icon={<span className="text-[26px] font-semibold leading-none text-[#03BFA5]">%</span>}>금리 안내</SectionTitle>
            <RateInfoCard product={product} />
          </div>
          <NoticeBox>{product.rateNotice}</NoticeBox>
        </section>
      </div>
    </div>
  );
}

export default function ProductDetail() {
  const { productId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const recommendationResult = useMemo(
    () => getActiveRecommendationResult(location),
    [location],
  );
  const product = useMemo(
    () => findProductViewById(recommendationResult, productId),
    [recommendationResult, productId],
  );
  const { accessToken } = useAuth();
  const [isApplicationFallbackOpen, setIsApplicationFallbackOpen] = useState(false);
  const [isFavoriteLoginOpen, setIsFavoriteLoginOpen] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);
  const leftPanelRef = useRef(null);
  const productInfoRef = useRef(null);
  const [rightPanelOffset, setRightPanelOffset] = useState(0);

  useEffect(() => {
    if (!recommendationResult) {
      navigate("/recommend", { replace: true });
    }
  }, [recommendationResult, navigate]);

  // "기본금리/최고금리" 카드가 왼쪽 "상품 안내" 카드와 같은 높이에서 시작하도록,
  // 태그 줄바꿈/제목 줄바꿈 등으로 달라지는 왼쪽 헤더 영역의 실제 높이를 측정해 오른쪽 패널에 반영한다.
  useLayoutEffect(() => {
    const panelEl = leftPanelRef.current;
    const infoEl = productInfoRef.current;
    if (!panelEl || !infoEl) return;

    const updateOffset = () => {
      setRightPanelOffset(infoEl.getBoundingClientRect().top - panelEl.getBoundingClientRect().top);
    };

    updateOffset();
    const observer = new ResizeObserver(updateOffset);
    observer.observe(panelEl);
    observer.observe(infoEl);
    return () => observer.disconnect();
  }, [product]);

  useEffect(() => {
    let cancelled = false;

    (async () => {
      if (!accessToken || !product?.productPropertyId) {
        if (!cancelled) setIsFavorite(false);
        return;
      }

      try {
        const res = await client.get(
          `/favorites/${product.productPropertyId}/status`,
          withAuth(accessToken),
        );
        if (!cancelled) setIsFavorite(Boolean(res.data));
      } catch (e) {
        console.error("찜 상태를 불러오지 못했습니다:", e);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [accessToken, product?.productPropertyId]);

  if (!recommendationResult) return null;

  if (!product) {
    return (
      <main className="flex min-h-screen flex-col items-center justify-center gap-6 bg-white text-[#454545]">
        <p className="text-[22px] font-medium">상품 정보를 찾을 수 없어요.</p>
        <button
          type="button"
          onClick={() => navigate("/products")}
          className="cursor-pointer rounded-[10px] border-2 border-[#03BFA5] px-6 py-3 text-[18px] font-medium text-[#03BFA5]"
        >
          상품 리스트로 돌아가기
        </button>
      </main>
    );
  }

  const handleApplication = () => {
    if (getProductApplyUrl(product).isFallback) {
      setIsApplicationFallbackOpen(true);
      return;
    }
    openProductApplication(product);
  };
  const handleFavorite = async () => {
    if (!accessToken) {
      setIsFavoriteLoginOpen(true);
      return;
    }
    if (!product.productPropertyId) return;

    const nextIsFavorite = !isFavorite;
    setIsFavorite(nextIsFavorite);
    try {
      if (nextIsFavorite) {
        await client.post(
          "/favorites",
          { productPropertyId: product.productPropertyId },
          withAuth(accessToken),
        );
      } else {
        await client.delete(
          `/favorites/${product.productPropertyId}`,
          withAuth(accessToken),
        );
      }
    } catch (e) {
      console.error("찜 처리에 실패했습니다:", e);
      setIsFavorite(!nextIsFavorite);
    }
  };

  return (
    <main className="min-h-screen bg-white pb-[80px] font-pretendard text-[#454545]">
      <div className="mx-auto mt-[13px] w-full max-w-[1320px] rounded-[3px] border border-[#D5D5D5] px-5 pb-[48px] pt-[32px] lg:px-[42px]">
        <div className="mx-auto grid w-full max-w-[1236px] gap-[24px] min-[1100px]:grid-cols-[minmax(0,1.17fr)_minmax(0,1fr)]">
          <LeftPanel
            product={product}
            onBack={() => navigate("/products")}
            panelRef={leftPanelRef}
            productInfoRef={productInfoRef}
          />
          <RightPanel
            product={product}
            onEditRate={() => navigate(`/products/${product.id}/calculator`)}
            onApply={handleApplication}
            onFavorite={handleFavorite}
            isLoggedIn={Boolean(accessToken)}
            topOffset={rightPanelOffset}
            isFavorite={isFavorite}
          />
        </div>
      </div>
      {isApplicationFallbackOpen && (
        <ApplicationFallbackModal
          product={product}
          onClose={() => setIsApplicationFallbackOpen(false)}
          onOpenInstitutionPage={() => {
            openProductApplication(product);
            setIsApplicationFallbackOpen(false);
          }}
        />
      )}
      {isFavoriteLoginOpen && (
        <LoginRequiredModal
          onClose={() => setIsFavoriteLoginOpen(false)}
          onLogin={() => navigate("/login")}
        />
      )}
    </main>
  );
}
