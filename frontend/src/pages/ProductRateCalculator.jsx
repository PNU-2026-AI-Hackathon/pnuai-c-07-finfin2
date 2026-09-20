import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {
  findProductViewById,
  getActiveRecommendationResult,
} from "../utils/recommendationResult";
import { openProductApplication } from "../utils/productApplyLink";

function ArrowLeftIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19 8 12l7-7" />
    </svg>
  );
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

function TaxIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h10M7 12h10M7 17h10" />
    </svg>
  );
}

function formatWon(value) {
  return `${Math.round(value).toLocaleString("ko-KR")} 원`;
}

function parseRate(rateText) {
  const value = Number(String(rateText).replace("%", ""));
  return Number.isFinite(value) ? value / 100 : 0;
}

function Chip({ label, active }) {
  return (
    <span
      className={`flex h-[38px] items-center justify-center rounded-full px-[18px] text-[18px] font-medium leading-[1.2] ${
        active
          ? "border-2 border-[#03BFA5] bg-[#F7FFFE] text-[#03BFA5]"
          : "border border-[#E4E4E4] bg-white text-[#454545]"
      }`}
    >
      {label}
    </span>
  );
}

function OptionButton({ active, children, onClick, className = "", activeTone = "mint", disabled = false }) {
  const activeClass = activeTone === "soft"
    ? "border border-[#E4E4E4] bg-[#EFEFEF] text-[#454545]"
    : "border-2 border-[#03BFA5] bg-[#F7FFFE] text-[#03BFA5]";

  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className={`flex h-[48px] items-center justify-center rounded-[8px] text-[18px] font-medium leading-[1.2] transition-colors ${
        active
          ? activeClass
          : "border border-[#E4E4E4] bg-white text-[#454545] hover:border-[#03BFA5]"
      } ${disabled ? "cursor-not-allowed opacity-70 hover:border-[#E4E4E4]" : ""} ${className}`}
    >
      {children}
    </button>
  );
}

function CalculatorCard({ children, className = "" }) {
  return (
    <section className={`rounded-[20px] border border-[#D5D5D5] bg-white ${className}`}>
      {children}
    </section>
  );
}

function AmountCard({ amount, label }) {
  return (
    <CalculatorCard className="relative h-[135px] px-[30px] py-[30px]">
      <h2 className="text-[18px] font-semibold leading-[1.2] text-[#454545]">{label}</h2>
      <div className="absolute bottom-[28px] right-[30px] flex items-end gap-[10px] text-[#454545]">
        <span className="text-[36px] font-bold leading-[1.2]">{amount.toLocaleString("ko-KR")}</span>
        <span className="pb-[7px] text-[21px] font-medium leading-[1.2]">원</span>
      </div>
    </CalculatorCard>
  );
}

function PeriodCard({ months, onChange, label = "저축 기간" }) {
  const options = [6, 12, 24, 36];

  return (
    <CalculatorCard className="relative h-[180px] px-[32px] py-[30px]">
      <h2 className="text-[18px] font-semibold leading-[1.2] text-[#454545]">{label}</h2>
      <div className="absolute right-[30px] top-[52px] flex items-end gap-[8px] text-[#454545]">
        <span className="text-[36px] font-bold leading-[1.2]">{months}</span>
        <span className="pb-[7px] text-[21px] font-medium leading-[1.2]">개월</span>
      </div>
      <div className="absolute bottom-[28px] right-[30px] flex gap-[5px]">
        {options.map((option) => (
          <OptionButton
            key={option}
            active={months === option}
            onClick={() => onChange(option)}
            className="h-[42px] rounded-full px-[18px]"
          >
            {option}개월
          </OptionButton>
        ))}
      </div>
    </CalculatorCard>
  );
}

function ToggleCard({ title, options, value, onChange, activeTone = "soft", disabled = false }) {
  return (
    <CalculatorCard className="h-[140px] px-[30px] py-[30px]">
      <h2 className="text-[18px] font-semibold leading-[1.2] text-[#454545]">{title}</h2>
      <div className="mt-[5px] grid grid-cols-2 gap-[5px]">
        {options.map((option) => (
          <OptionButton key={option} active={value === option} onClick={() => onChange(option)} activeTone={activeTone} disabled={disabled}>
            {option}
          </OptionButton>
        ))}
      </div>
    </CalculatorCard>
  );
}

function RateHero({ calculator, onEditRate }) {

  return (
    <section className="relative flex min-h-[150px] items-center justify-between gap-6 rounded-[10px] bg-[#EFFFFD] px-[30px] py-[28px]">
      <button
        type="button"
        onClick={onEditRate}
        className="absolute right-[30px] top-[25px] flex items-center gap-[10px] text-[16px] font-medium leading-[1.2] text-[#454545] transition-colors hover:text-[#03BFA5]"
      >
        <ArrowLeftIcon className="size-[18px]" />
        계산 금리 수정하기
      </button>
      <div>
        <p className="mb-[12px] text-[18px] font-semibold leading-[1.2] text-[#454545]">내가 받을 수 있는 금리</p>
        <div className="flex flex-wrap items-end gap-[6px]">
          <span className="text-[40px] font-bold leading-[1.2] text-[#03BFA5]">{calculator.headlineRate}</span>
          <span className="pb-[8px] text-[18px] font-medium leading-[1.2] text-[#454545]">(연)</span>
          <span className="pb-[9px] text-[16px] font-medium leading-[1.2] text-[#454545]">{calculator.baseText}</span>
        </div>
      </div>
      <div className="mt-[55px] flex flex-wrap justify-end gap-[5px]">
        {calculator.conditions.map((chip) => (
          <Chip key={chip.label} label={chip.label} active={chip.active} />
        ))}
      </div>
    </section>
  );
}

function RateEditModal({ calculator, onClose, onSave }) {
  const [conditions, setConditions] = useState(calculator.conditions);
  const rate = calculator.baseRate + conditions
    .filter((condition) => condition.active)
    .reduce((sum, condition) => sum + condition.rate, 0);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4" role="presentation">
      <section role="dialog" aria-modal="true" aria-labelledby="rate-edit-title" className="w-full max-w-[620px] rounded-[20px] bg-white p-8 shadow-xl">
        <div className="flex items-start justify-between gap-6">
          <div>
            <h2 id="rate-edit-title" className="text-[28px] font-bold text-[#454545]">계산 금리 수정하기</h2>
            <p className="mt-2 text-[18px] text-[#606060]">적용할 우대금리 조건을 선택해 주세요.</p>
          </div>
          <button type="button" onClick={onClose} aria-label="닫기" className="text-[28px] leading-none text-[#606060]">×</button>
        </div>
        <div className="mt-7 rounded-[10px] bg-[#EFFFFD] px-6 py-5">
          <p className="text-[18px] font-medium text-[#454545]">적용 금리</p>
          <p className="mt-1 text-[38px] font-bold text-[#03BFA5]">연 {rate.toLocaleString("ko-KR", { maximumFractionDigits: 2 })}%</p>
        </div>
        <div className="mt-5 flex flex-col gap-3">
          {conditions.length > 0 ? conditions.map((condition, index) => (
            <label key={`${condition.id}-${index}`} className="flex cursor-pointer items-center justify-between rounded-[10px] border border-[#E4E4E4] px-5 py-4 text-[18px] text-[#454545]">
              <span className="flex items-center gap-3">
                <input
                  type="checkbox"
                  checked={condition.active}
                  onChange={() => setConditions((current) => current.map((item, itemIndex) => itemIndex === index ? { ...item, active: !item.active } : item))}
                  className="size-5 accent-[#03BFA5]"
                />
                {condition.label}
              </span>
              <span className="font-semibold text-[#03BFA5]">+{condition.rate}%</span>
            </label>
          )) : <p className="py-6 text-center text-[18px] text-[#606060]">수정할 우대금리 조건이 없어요.</p>}
        </div>
        <div className="mt-8 grid grid-cols-2 gap-3">
          <button type="button" onClick={onClose} className="h-14 rounded-[10px] border border-[#D5D5D5] text-[18px] font-semibold text-[#454545]">취소</button>
          <button type="button" onClick={() => onSave(conditions)} className="h-14 rounded-[10px] bg-[#03BFA5] text-[18px] font-semibold text-white">적용하기</button>
        </div>
      </section>
    </div>
  );
}

function ResultRow({ label, value, tone = "default" }) {
  const colorClass = tone === "positive" ? "text-[#03BFA5]" : tone === "negative" ? "text-[#D3455B]" : "text-[#454545]";

  return (
    <div className="flex h-[38px] items-start justify-between border-b border-[#D9D9D9] text-[20px] font-medium leading-[1.2]">
      <span className="text-[#454545]">{label}</span>
      <span className={colorClass}>{value}</span>
    </div>
  );
}

function ResultCard({ principal, interest, tax, finalAmount }) {
  return (
    <CalculatorCard className="h-[335px] px-[30px] py-[30px]">
      <h2 className="text-[18px] font-semibold leading-[1.2] text-[#454545]">예상 수령액</h2>
      <div className="mt-[28px] flex flex-col gap-[12px]">
        <ResultRow label="납입원금합계" value={formatWon(principal)} />
        <ResultRow label="세전 이자" value={`+ ${formatWon(interest)}`} tone="positive" />
        <div className="flex items-center justify-between text-[20px] font-medium leading-[1.2]">
          <span className="text-[#454545]">이자 과세 (15.4%)</span>
          <span className="flex items-center gap-[5px] text-[#D3455B]">
            <TaxIcon className="size-[18px]" />
            - {formatWon(tax)}
          </span>
        </div>
      </div>
      <div className="mt-[27px] border-t-2 border-[#D9D9D9] pt-[20px]">
        <div className="flex items-center justify-between">
          <span className="text-[20px] font-semibold leading-[1.2] text-[#454545]">세후 실수령액</span>
          <span className="text-[33px] font-semibold leading-[1.2] text-[#03BFA5]">{formatWon(finalAmount)}</span>
        </div>
      </div>
    </CalculatorCard>
  );
}

function BottomActions({ product }) {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col gap-[10px]">
      <div className="grid grid-cols-2 gap-[10px]">
        <button
          type="button"
          onClick={() => navigate(`/products/${product.id}`)}
          className="flex h-[64px] items-center justify-center gap-[8px] rounded-[10px] border-2 border-[#03BFA5] bg-white text-[20px] font-semibold leading-[1.2] text-[#03BFA5]"
        >
          <ArrowLeftIcon className="size-[16px]" />
          상품 상세
        </button>
        <button
          type="button"
          onClick={() => openProductApplication(product)}
          className="flex h-[64px] items-center justify-center rounded-[10px] border-2 border-[#03BFA5] bg-[#03BFA5] text-[20px] font-semibold leading-[1.2] text-white"
        >
          신청하러 가기
        </button>
      </div>
      <div className="flex min-h-[76px] items-center rounded-[10px] border border-[#03BFA5] bg-[#F7FFFE] px-[30px] py-[18px]">
        <InfoCircleIcon className="mr-[9px] size-[20px] shrink-0 text-[#03BFA5]" />
        <p className="text-[15px] font-normal leading-[1.3] text-[#454545]">
          매월 초 ∙ 매월 동일액 납입 가정 기준 예상치입니다(첫 회차가 전 기간, 마지막 회차가 1개월치 이자) 실제 납입 시점 ∙ 금액에 따라 수령액이 달라질 수 있습니다.
        </p>
      </div>
    </div>
  );
}

export default function ProductRateCalculator() {
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

  useEffect(() => {
    if (!recommendationResult) {
      navigate("/recommend", { replace: true });
    }
  }, [recommendationResult, navigate]);

  const [months, setMonths] = useState(product?.calculator?.months ?? 12);
  const [accumulationType, setAccumulationType] = useState(product?.calculator?.accumulationType ?? "정액");
  const [interestType, setInterestType] = useState(product?.calculator?.interestType ?? "단리");
  const [taxType, setTaxType] = useState(product?.calculator?.taxType ?? "일반 15.4%");
  const [conditions, setConditions] = useState(product?.calculator?.conditions ?? []);
  const [isRateEditOpen, setIsRateEditOpen] = useState(false);
  const isDeposit = product?.detail?.productType === "DEPOSIT";

  const calculator = useMemo(() => {
    if (!product) return null;

    const baseRate = product.calculator.baseRate ?? 0;
    const preferentialRate = conditions
      .filter((condition) => condition.active)
      .reduce((sum, condition) => sum + condition.rate, 0);
    const headlineRate = baseRate + preferentialRate;

    return {
      ...product.calculator,
      conditions,
      headlineRate: `${headlineRate.toLocaleString("ko-KR", { maximumFractionDigits: 2 })}%`,
      baseText: `기본 ${baseRate.toLocaleString("ko-KR", { maximumFractionDigits: 2 })}% + 충족 우대 ${preferentialRate.toLocaleString("ko-KR", { maximumFractionDigits: 2 })}%`,
    };
  }, [conditions, product]);

  const result = useMemo(() => {
    if (!product) return { principal: 0, interest: 0, tax: 0, finalAmount: 0 };

    const monthlyAmount = product.calculator.monthlyAmount;
    const principal = isDeposit ? monthlyAmount : monthlyAmount * months;
    const rate = parseRate(calculator?.headlineRate);
    const simpleInterest = isDeposit
      ? monthlyAmount * rate * months / 12
      : monthlyAmount * rate * months * (months + 1) / 24;
    const interest = interestType === "복리" ? simpleInterest * 1.015 : simpleInterest;
    const tax = taxType.includes("비과세") ? 0 : interest * 0.154;

    return {
      principal,
      interest,
      tax,
      finalAmount: principal + interest - tax,
    };
  }, [calculator, interestType, isDeposit, months, product, taxType]);

  if (!recommendationResult) return null;

  if (!product) {
    return (
      <main className="flex min-h-screen flex-col items-center justify-center gap-6 bg-white text-[#454545]">
        <p className="text-[22px] font-medium">상품 정보를 찾을 수 없어요.</p>
        <button
          type="button"
          onClick={() => navigate("/products")}
          className="rounded-[10px] border-2 border-[#03BFA5] px-6 py-3 text-[18px] font-medium text-[#03BFA5]"
        >
          상품 리스트로 돌아가기
        </button>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-white pb-[80px] font-pretendard text-[#454545]">
      <div className="mx-auto mt-[13px] w-full max-w-[1320px] rounded-[3px] border border-[#D5D5D5] px-5 pb-[48px] pt-[32px] lg:px-[42px]">
        <div className="mx-auto w-full max-w-[1236px]">
          <h1 className="mb-[26px] text-[28px] font-bold leading-[1.2] text-[#454545]">{isDeposit ? "예금" : "적금"} 수익률 계산기</h1>
          <RateHero calculator={calculator} onEditRate={() => setIsRateEditOpen(true)} />

          <div className="mt-[24px] grid gap-[24px] min-[1100px]:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
            <div className="flex flex-col gap-[16px]">
              <AmountCard amount={product.calculator.monthlyAmount} label={isDeposit ? "예치 금액" : "월 납입액"} />
              <PeriodCard months={months} onChange={setMonths} label={isDeposit ? "예치 기간" : "저축 기간"} />
              {!isDeposit && (
                <ToggleCard
                  title="적립 방식"
                  options={["정액", "자유"]}
                  value={accumulationType}
                  onChange={setAccumulationType}
                  disabled
                />
              )}
              <ToggleCard
                title="이자 방식"
                options={["단리", "복리"]}
                value={interestType}
                onChange={setInterestType}
                disabled
              />
              <ToggleCard
                title="과세 유형"
                options={["일반 15.4%", "비과세 0%"]}
                value={taxType}
                onChange={setTaxType}
                activeTone="mint"
              />
            </div>

            <div className="flex flex-col gap-[10px]">
              <ResultCard {...result} />
              <BottomActions product={product} />
            </div>
          </div>
        </div>
      </div>
      {isRateEditOpen && (
        <RateEditModal
          calculator={calculator}
          onClose={() => setIsRateEditOpen(false)}
          onSave={(nextConditions) => {
            setConditions(nextConditions);
            setIsRateEditOpen(false);
          }}
        />
      )}
    </main>
  );
}
