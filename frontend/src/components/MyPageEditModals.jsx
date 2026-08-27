import { useState } from "react";
import Tag from "./Tag";
import { FormInput, FormSelect } from "./FormFields";
import InfoBox from "./InfoBox";
import { BankSelector } from "./RecommendSteps";
import { findCategoryOptions, findSelectedOptionIds, replaceCategorySelection } from "../hooks/UseMyPage";

const BANK_CATEGORIES = [
  { id: "시중", title: "시중은행", banks: ["KB국민", "신한", "하나", "우리", "SC제일", "iM뱅크"] },
  { id: "인터넷", title: "인터넷은행", banks: ["카카오뱅크", "토스뱅크", "케이뱅크"] },
  { id: "특수", title: "특수은행", banks: ["NH농협", "Sh수협", "IBK기업"] },
  { id: "지방", title: "지방은행", banks: ["BNK부산", "광주은행", "제주은행", "전북은행", "BNK경남은행"] },
];

const INCOME_LEVELS = [
  { percent: 60, guide: "월 154만원 이하" },
  { percent: 80, guide: "월 205만원 이하" },
  { percent: 100, guide: "월 256만원 이하" },
  { percent: 120, guide: "월 308만원 이하" },
  { percent: 150, guide: "월 385만원 이하" },
  { percent: 180, guide: "" },
];

const YEARS = Array.from({ length: 50 }, (_, i) => 1975 + i);
const MONTHS = Array.from({ length: 12 }, (_, i) => i + 1);
const DAYS = Array.from({ length: 31 }, (_, i) => i + 1);

function CloseIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18 18 6M6 6l12 12" />
    </svg>
  );
}

function CheckIcon({ className = "" }) {
  return (
    <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={4} d="M5 13l4 4L19 7" />
    </svg>
  );
}

function getRangeProgress(value, min, max) {
  const numeric = Number(value);
  return `${Math.min(100, Math.max(0, ((numeric - min) / (max - min)) * 100))}%`;
}

function parseBirthdate(birthdate) {
  if (!birthdate) return { year: "", month: "", day: "" };
  const [year, month, day] = birthdate.split("-");
  return { year, month: Number(month), day: Number(day) };
}

function ModalShell({ title, required, choiceHint, onClose, onSave, saveDisabled, wide = false, children }) {
  return (
    <div
      className="fixed inset-0 z-[100] flex items-center justify-center bg-black/40 px-4"
      role="presentation"
      onMouseDown={onClose}
    >
      <div
        role="dialog"
        aria-modal="true"
        className={`max-h-[calc(100vh-48px)] w-full overflow-y-auto rounded-[20px] bg-white p-8 ${wide ? "max-w-[640px]" : "max-w-[460px]"}`}
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="mb-1 flex items-start justify-between gap-4">
          <div className="flex flex-wrap items-baseline gap-1.5">
            <h3 className="text-[19px] font-bold text-[#181818]">{title}</h3>
            {required && <span className="text-[15px] font-semibold text-[#03BFA5]">*</span>}
            {choiceHint && <span className="text-[13px] font-normal text-[#454545]">{choiceHint}</span>}
          </div>
          <button type="button" onClick={onClose} aria-label="닫기" className="shrink-0 text-[#A5A5A5] hover:text-[#606060]">
            <CloseIcon className="size-5" />
          </button>
        </div>
        {required && <p className="mb-5 text-[14px] text-[#454545]">필수 입력 항목입니다.</p>}

        <div className={required ? "" : "mt-4"}>{children}</div>

        <div className="mt-9 flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            className="rounded-full border border-[#E0DFDF] px-6 py-1 text-[16px] font-medium text-[#454545] transition-colors hover:border-[#03BFA5] hover:text-[#03BFA5]"
          >
            취소
          </button>
          <button
            type="button"
            onClick={onSave}
            disabled={saveDisabled}
            className="rounded-full bg-[#03BFA5] px-6 py-1 text-[16px] font-medium text-white transition-colors hover:bg-[#02A892] disabled:cursor-not-allowed disabled:bg-[#BFEAE3]"
          >
            저장
          </button>
        </div>
      </div>
    </div>
  );
}

function BirthdateModal({ profile, onClose, onSave }) {
  const initial = parseBirthdate(profile.birthdate);
  const [year, setYear] = useState(initial.year);
  const [month, setMonth] = useState(initial.month);
  const [day, setDay] = useState(initial.day);
  const isComplete = Boolean(year && month && day);

  const handleSave = () => {
    if (!isComplete) return;
    const mm = String(month).padStart(2, "0");
    const dd = String(day).padStart(2, "0");
    onSave({ birthdate: `${year}-${mm}-${dd}` });
  };

  return (
    <ModalShell title="생년월일" required onClose={onClose} onSave={handleSave} saveDisabled={!isComplete}>
      <div className="flex flex-wrap items-center gap-3">
        {[
          { items: YEARS, value: year, setValue: setYear, unit: "년" },
          { items: MONTHS, value: month, setValue: setMonth, unit: "월" },
          { items: DAYS, value: day, setValue: setDay, unit: "일" },
        ].map(({ items, value, setValue, unit }) => (
          <div key={unit} className="flex items-center gap-1.5">
            <FormSelect value={value} onChange={(e) => setValue(e.target.value)} className="h-11 w-[92px] bg-white text-[15px]">
              <option value="">선택</option>
              {items.map((v) => (
                <option key={v} value={v}>
                  {v}
                </option>
              ))}
            </FormSelect>
            <span className="text-[14px] font-medium text-[#454545]">{unit}</span>
          </div>
        ))}
      </div>
    </ModalShell>
  );
}

function IncomeModal({ profile, onClose, onSave }) {
  const [income, setIncome] = useState(profile.annualIncome !== null && profile.annualIncome !== undefined ? String(profile.annualIncome) : "");
  const isComplete = income !== "";

  const handleSave = () => {
    if (!isComplete) return;
    onSave({ annualIncome: Number(income) });
  };

  return (
    <ModalShell title="개인 연 소득" required onClose={onClose} onSave={handleSave} saveDisabled={!isComplete}>
      <div className="flex h-12 w-full items-stretch overflow-hidden rounded-[8px] border border-[#D9D9D9]">
        <FormInput
          type="text"
          inputMode="numeric"
          placeholder="숫자(단위:만원)를 입력하세요."
          value={income}
          onChange={(e) => {
            const digits = e.target.value.replace(/[^0-9]/g, "");
            const num = digits === "" ? "" : String(Math.min(10000, Number(digits)));
            setIncome(num);
          }}
          className="h-full flex-1 border-none px-4 text-[15px] focus:ring-0"
        />
        <div className="flex items-center border-l border-[#D9D9D9] bg-[#FAFAFA] px-4 text-[14px] text-[#454545]">만원</div>
      </div>
      <InfoBox type="mint-rect" className="mt-3 h-auto py-2.5">
        0원~1억원 내에서 입력 가능합니다.
      </InfoBox>
    </ModalShell>
  );
}

function MonthlyGoalModal({ profile, onClose, onSave }) {
  const [amount, setAmount] = useState(profile.monthlySavingsGoal || 1);

  const handleSave = () => onSave({ monthlySavingsGoal: Number(amount) });

  return (
    <ModalShell title="월 납입 희망액" required onClose={onClose} onSave={handleSave} saveDisabled={false}>
      <input
        type="range"
        min={1}
        max={100}
        value={amount}
        onChange={(e) => setAmount(Number(e.target.value))}
        className="recommend-range mb-2 w-full"
        style={{ "--range-progress": getRangeProgress(amount, 1, 100) }}
      />
      <div className="mb-5 flex justify-between text-[13px] font-medium text-[#454545]">
        <span>1만원</span>
        <span>100만원</span>
      </div>
      <div className="flex h-11 w-[160px] items-center gap-2 rounded-full border border-[#CACACA] bg-white px-4 shadow-sm">
        <FormInput
          type="text"
          inputMode="numeric"
          value={amount}
          onChange={(e) => {
            const digits = e.target.value.replace(/[^0-9]/g, "");
            if (digits === "") return setAmount("");
            setAmount(Math.min(100, Math.max(1, Number(digits))));
          }}
          className="w-12 border-none bg-transparent p-0 text-center text-[14px] font-bold focus:ring-0"
        />
        <span className="text-[14px] font-semibold text-[#454545]">만원</span>
      </div>
    </ModalShell>
  );
}

function HouseholdModal({ profile, onClose, onSave }) {
  const [count, setCount] = useState(profile.householdSize || 1);
  const [percent, setPercent] = useState(profile.householdIncomePercent || null);

  const handleSave = () => {
    if (!percent) return;
    onSave({ householdSize: count, householdIncomePercent: percent });
  };

  return (
    <ModalShell title="가구원 수 · 가구 소득" required onClose={onClose} onSave={handleSave} saveDisabled={!percent}>
      <p className="mb-2 text-[14px] font-medium text-[#454545]">가구원 수</p>
      <div className="mb-5 flex h-12 w-[160px] items-center justify-center gap-4 rounded-full border border-[#CACACA] bg-white shadow-sm">
        <button
          type="button"
          onClick={() => setCount((prev) => Math.max(1, prev - 1))}
          className="flex size-7 items-center justify-center rounded-full border-2 border-[#454545] text-[16px] leading-none text-[#454545] hover:bg-gray-50"
        >
          −
        </button>
        <span className="text-[16px] text-[#181818]">{count}인</span>
        <button
          type="button"
          onClick={() => setCount((prev) => prev + 1)}
          className="flex size-7 items-center justify-center rounded-full border-2 border-[#454545] text-[16px] leading-none text-[#454545] hover:bg-gray-50"
        >
          +
        </button>
      </div>

      <p className="mb-2 text-[14px] font-medium text-[#454545]">가구 소득</p>
      <div className="mb-3 flex flex-col gap-2">
        {INCOME_LEVELS.map((item) => {
          const isSelected = percent === item.percent;
          return (
            <label
              key={item.percent}
              className={`flex h-12 cursor-pointer items-center gap-2.5 rounded-full border-2 px-5 transition-all ${
                isSelected ? "border-[#03BFA5] bg-[#03BFA5] text-white" : "border-[#E0DFDF] bg-white hover:border-[#03BFA5]"
              }`}
            >
              <input
                type="checkbox"
                className="hidden"
                checked={isSelected}
                onChange={() => setPercent(isSelected ? null : item.percent)}
              />
              <span
                className={`flex size-4 items-center justify-center rounded-[3px] ${
                  isSelected ? "bg-white" : "border border-[#454545] bg-white"
                }`}
              >
                {isSelected && <CheckIcon className="size-3 text-[#03BFA5]" />}
              </span>
              <span className="flex-1 text-[14px]">중위소득 {item.percent}%</span>
              {item.guide && <span className={`text-[13px] ${isSelected ? "text-white" : "text-[#03BFA5]"}`}>{item.guide}</span>}
            </label>
          );
        })}
      </div>
      <InfoBox type="mint-pill" className="h-auto w-full py-2.5">
        가구원 수에 따른 중위소득 기준으로 선택해 주세요.
      </InfoBox>
    </ModalShell>
  );
}

function RegionModal({ profile, categories, onClose, onSave }) {
  const options = findCategoryOptions(categories, "regions");
  const currentId = findSelectedOptionIds(categories, "regions", profile.selectedOptionIds)[0] ?? "";
  const [selectedId, setSelectedId] = useState(currentId);

  const handleSave = () => {
    const newIds = selectedId ? [Number(selectedId)] : [];
    onSave({ selectedOptionIds: replaceCategorySelection(profile.selectedOptionIds, categories, "regions", newIds) });
  };

  return (
    <ModalShell title="거주 지역" onClose={onClose} onSave={handleSave}>
      <FormSelect value={selectedId} onChange={(e) => setSelectedId(e.target.value)} className="h-12 w-full bg-white text-[15px]">
        <option value="">선택해주세요.</option>
        {options.map((option) => (
          <option key={option.optionId} value={option.optionId}>
            {option.optionValue}
          </option>
        ))}
      </FormSelect>
      <InfoBox type="mint-region" className="mt-3 h-auto w-full py-2.5">
        지자체별 청년 금융상품 필터링에 활용됩니다.
      </InfoBox>
    </ModalShell>
  );
}

function SingleChoiceOptionModal({ title, categoryName, profile, categories, onClose, onSave, required = false }) {
  const options = findCategoryOptions(categories, categoryName);
  const currentId = findSelectedOptionIds(categories, categoryName, profile.selectedOptionIds)[0] ?? null;
  const [selectedId, setSelectedId] = useState(currentId);

  const handleSave = () => {
    const newIds = selectedId ? [selectedId] : [];
    onSave({ selectedOptionIds: replaceCategorySelection(profile.selectedOptionIds, categories, categoryName, newIds) });
  };

  return (
    <ModalShell
      title={title}
      choiceHint="(단일 선택)"
      required={required}
      onClose={onClose}
      onSave={handleSave}
      saveDisabled={required && !selectedId}
    >
      <div className="flex flex-wrap gap-2.5">
        {options.map((option) => (
          <Tag
            key={option.optionId}
            label={option.optionValue}
            selected={selectedId === option.optionId}
            onClick={() => setSelectedId((prev) => (prev === option.optionId ? null : option.optionId))}
          />
        ))}
      </div>
    </ModalShell>
  );
}

function MultiChoiceOptionModal({ title, categoryName, profile, categories, onClose, onSave, required = false }) {
  const options = findCategoryOptions(categories, categoryName);
  const currentIds = findSelectedOptionIds(categories, categoryName, profile.selectedOptionIds);
  const [selectedIds, setSelectedIds] = useState(currentIds);

  const toggle = (id) => {
    setSelectedIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
  };

  const handleSave = () => {
    onSave({ selectedOptionIds: replaceCategorySelection(profile.selectedOptionIds, categories, categoryName, selectedIds) });
  };

  return (
    <ModalShell
      title={title}
      choiceHint="(복수 선택)"
      required={required}
      onClose={onClose}
      onSave={handleSave}
      saveDisabled={required && selectedIds.length === 0}
    >
      <div className="flex flex-wrap gap-2.5">
        {options.map((option) => (
          <Tag
            key={option.optionId}
            label={option.optionValue}
            selected={selectedIds.includes(option.optionId)}
            onClick={() => toggle(option.optionId)}
          />
        ))}
      </div>
    </ModalShell>
  );
}

function TenureModal({ profile, onClose, onSave }) {
  const [months, setMonths] = useState(profile.tenureMonths || 0);
  const [isFirstJob, setIsFirstJob] = useState(Boolean(profile.isFirstJob));

  const handleSave = () => onSave({ tenureMonths: Number(months), isFirstJob });

  return (
    <ModalShell title="근속 기간" onClose={onClose} onSave={handleSave}>
      <input
        type="range"
        min={0}
        max={120}
        value={months}
        onChange={(e) => setMonths(Number(e.target.value))}
        className="recommend-range mb-2 w-full"
        style={{ "--range-progress": getRangeProgress(months, 0, 120) }}
      />
      <div className="mb-5 flex justify-between text-[13px] font-medium text-[#454545]">
        <span>0개월</span>
        <span>120개월</span>
      </div>

      <div className="mb-3 flex h-11 w-[130px] items-center gap-2 rounded-full border border-[#D9D9D9] bg-white px-4">
        <FormInput
          type="text"
          inputMode="numeric"
          value={months}
          onChange={(e) => {
            const digits = e.target.value.replace(/[^0-9]/g, "");
            if (digits === "") return setMonths("");
            setMonths(Math.min(120, Math.max(0, Number(digits))));
          }}
          className="w-12 border-none bg-transparent p-0 text-center text-[14px] focus:ring-0"
        />
        <span className="text-[14px] text-[#454545]">개월</span>
      </div>

      <label
        className={`mb-3 flex h-11 cursor-pointer items-center gap-2.5 rounded-full border px-5 transition-all ${
          isFirstJob ? "border-[#03BFA5] bg-[#03BFA5] text-white" : "border-[#D9D9D9] bg-white text-[#454545] hover:border-[#03BFA5]"
        }`}
      >
        <input type="checkbox" className="hidden" checked={isFirstJob} onChange={(e) => setIsFirstJob(e.target.checked)} />
        <span className={`flex size-4 items-center justify-center rounded-[3px] ${isFirstJob ? "bg-white" : "border border-gray-300 bg-white"}`}>
          {isFirstJob && <CheckIcon className="size-3 text-[#03BFA5]" />}
        </span>
        <span className="text-[14px]">첫 직장입니다.</span>
      </label>

      <InfoBox type="mint-pill" className="h-auto w-full py-2.5">
        일부 정부 상품은 근속 요건이 존재합니다.
      </InfoBox>
    </ModalShell>
  );
}

function CheckableRow({ checked, onChange, label }) {
  return (
    <label
      className={`flex h-12 cursor-pointer items-center gap-2.5 rounded-lg border px-5 transition-all ${
        checked ? "border-transparent bg-[#03BFA5] text-white" : "border-[#E0DFDF] bg-white text-[#454545] hover:border-[#03BFA5]"
      }`}
    >
      <input type="checkbox" className="hidden" checked={checked} onChange={onChange} />
      <span className={`flex size-4 items-center justify-center rounded-[3px] ${checked ? "bg-white" : "border border-[#454545] bg-white"}`}>
        {checked && <CheckIcon className="size-3 text-[#03BFA5]" />}
      </span>
      <span className="text-[14px]">{label}</span>
    </label>
  );
}

function HousingModal({ profile, onClose, onSave }) {
  const initialStatus = profile.isHomeless == null ? "" : profile.isHomeless ? "무주택" : "유주택";
  const [housingStatus, setHousingStatus] = useState(initialStatus);
  const [isHouseholder, setIsHouseholder] = useState(Boolean(profile.isHouseholder));

  const handleSave = () => {
    onSave({
      isHomeless: housingStatus === "" ? null : housingStatus === "무주택",
      isHouseholder,
    });
  };

  return (
    <ModalShell title="무주택 여부" onClose={onClose} onSave={handleSave}>
      <div className="flex flex-col gap-2.5">
        <CheckableRow
          checked={housingStatus === "무주택"}
          onChange={() => setHousingStatus((prev) => (prev === "무주택" ? "" : "무주택"))}
          label="무주택"
        />
        <CheckableRow
          checked={housingStatus === "유주택"}
          onChange={() => setHousingStatus((prev) => (prev === "유주택" ? "" : "유주택"))}
          label="유주택"
        />
        <CheckableRow checked={isHouseholder} onChange={(e) => setIsHouseholder(e.target.checked)} label="세대주입니다." />
      </div>
      <InfoBox type="mint-pill" className="mt-3 h-auto w-full py-2.5">
        청약/주거 지원 상품의 자격 요건입니다.
      </InfoBox>
    </ModalShell>
  );
}

function TransactionModal({ profile, onClose, onSave }) {
  const [subStep, setSubStep] = useState(1);
  const [neverUsedBanks, setNeverUsedBanks] = useState(profile.neverUsedBanks || []);
  const [maturedSavingBanks, setMaturedSavingBanks] = useState(profile.maturedSavingBanks || []);

  const handlePrimaryAction = () => {
    if (subStep === 1) {
      setSubStep(2);
      return;
    }
    onSave({ neverUsedBanks, maturedSavingBanks });
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/40 px-4" role="presentation" onMouseDown={onClose}>
      <div
        role="dialog"
        aria-modal="true"
        className="max-h-[calc(100vh-48px)] w-full max-w-[720px] overflow-y-auto rounded-[20px] bg-white p-8"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="mb-4 flex items-start justify-between gap-4">
          <h3 className="text-[19px] font-bold text-[#181818]">거래 이력</h3>
          <button type="button" onClick={onClose} aria-label="닫기" className="shrink-0 text-[#A5A5A5] hover:text-[#606060]">
            <CloseIcon className="size-5" />
          </button>
        </div>

        {subStep === 1 ? (
          <BankSelector
            theme="mint"
            title="첫거래 은행"
            infoText="아직 거래해본 적 없는 은행을 선택하면, 해당 은행의 '첫거래 우대금리'가 자동 반영됩니다."
            cats={{ bankCategories: BANK_CATEGORIES }}
            selectedBanks={neverUsedBanks}
            onChange={setNeverUsedBanks}
          />
        ) : (
          <BankSelector
            theme="blue"
            title="만기 예적금이 있는 은행"
            infoText="만기된(될) 예적금이 있는 은행을 선택하면 해당 은행의 '재예치 우대금리'가 자동 반영됩니다."
            cats={{ bankCategories: BANK_CATEGORIES }}
            selectedBanks={maturedSavingBanks}
            onChange={setMaturedSavingBanks}
            disabledBanks={neverUsedBanks}
          />
        )}

        <div className="mt-4 flex items-center justify-end gap-2">
          {subStep === 2 && (
            <button
              type="button"
              onClick={() => setSubStep(1)}
              className="mr-auto text-[14px] font-medium text-[#454545] hover:text-[#454545]"
            >
              ← 이전
            </button>
          )}
          <button
            type="button"
            onClick={onClose}
            className="rounded-full border border-[#E0DFDF] px-6 py-1 text-[16px] font-medium text-[#454545] transition-colors hover:border-[#03BFA5] hover:text-[#03BFA5]"
          >
            취소
          </button>
          <button
            type="button"
            onClick={handlePrimaryAction}
            className="rounded-full bg-[#03BFA5] px-6 py-1 text-[16px] font-medium text-white transition-colors hover:bg-[#02A892]"
          >
            저장
          </button>
        </div>
      </div>
    </div>
  );
}

export default function EditFieldModal({ fieldKey, profile, categories, onClose, onSave }) {
  if (!profile || !categories) return null;

  switch (fieldKey) {
    case "birthdate":
      return <BirthdateModal profile={profile} onClose={onClose} onSave={onSave} />;
    case "income":
      return <IncomeModal profile={profile} onClose={onClose} onSave={onSave} />;
    case "monthlyGoal":
      return <MonthlyGoalModal profile={profile} onClose={onClose} onSave={onSave} />;
    case "household":
      return <HouseholdModal profile={profile} onClose={onClose} onSave={onSave} />;
    case "region":
      return <RegionModal profile={profile} categories={categories} onClose={onClose} onSave={onSave} />;
    case "tenure":
      return <TenureModal profile={profile} onClose={onClose} onSave={onSave} />;
    case "housing":
      return <HousingModal profile={profile} onClose={onClose} onSave={onSave} />;
    case "transaction":
      return <TransactionModal profile={profile} onClose={onClose} onSave={onSave} />;
    case "savingPeriod":
      return (
        <SingleChoiceOptionModal
          title="저축 기간"
          categoryName="savingPeriod"
          required
          profile={profile}
          categories={categories}
          onClose={onClose}
          onSave={onSave}
        />
      );
    case "status":
      return (
        <SingleChoiceOptionModal
          title="현재 신분"
          categoryName="status"
          profile={profile}
          categories={categories}
          onClose={onClose}
          onSave={onSave}
        />
      );
    case "benefits":
      return (
        <MultiChoiceOptionModal
          title="핵심 혜택"
          categoryName="benefits"
          profile={profile}
          categories={categories}
          onClose={onClose}
          onSave={onSave}
        />
      );
    case "bankRelation":
      return (
        <MultiChoiceOptionModal
          title="은행 거래"
          categoryName="bankRelation"
          required
          profile={profile}
          categories={categories}
          onClose={onClose}
          onSave={onSave}
        />
      );
    default:
      return null;
  }
}
