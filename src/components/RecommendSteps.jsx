import StepLayout from "./StepLayout";
import NavButtons from "./NavButtons";
import InfoBox from "./InfoBox";
import Tag from "./Tag";
import { FormInput, FormSelect } from "./FormFields";
import { toggleField } from "../utils/toggleField";

// 1. 저축 계획 (Step 1-1)
export function StepSavingPlan({ data, setData, cats, onNext}) {
  const amount = data.monthlyAmount || 1;
  return (
    <StepLayout step={1} title="기본 정보" sub= "몇 가지 간단한 키워드 태그로 당신에게 Fin. 한 상품을 찾아드립니다.">
      <p className="text-[18px] font-semibold text-[#454545] mb-5">저축 계획</p>

      <div className="pl-2">
        <div className="flex items-center gap-1.5 mb-4">
          <p className="text-[15px] text-[#454545]">월 납입 희망액</p>
          <span className="text-[#03BFA5] text-[15px]">ⓘ</span>
        </div>

        <div className="w-full max-w-112.5">
          <input type="range" min={1} max={100} value={amount}
            onChange={(e) => setData({ ...data, monthlyAmount: Number(e.target.value) })}
            className="w-110 h-1 bg-[#D0D0D0] rounded-lg appearance-none cursor-pointer accent-[#03BFA5] mb-1" />
          
          <div className="flex justify-between text-[13px] text-[#454545] mb-10 px-0.5">
            <span>1만원</span>
            <span>100만원</span>
          </div>
        </div>

        <div className="flex items-center gap-1 border border-gray-300 rounded-full px-4 py-2 w-fit mb-12 bg-white">
          <input type="number" value={amount}
            onChange={(e) => setData({ ...data, monthlyAmount: Math.min(100, Math.max(1, Number(e.target.value))) })}
            className="w-10 text-center text-[#CACACA] text-sm font-medium focus:outline-none bg-transparent" />
          <span className="text-sm text-[#454545]">만원</span>
        </div>
      </div>
      <NavButtons isFirst onNext={onNext} />
    </StepLayout>
  );
}

// 2. 현재 신분 + 희망 저축 기간 (Step 1-2)
export function StepBasicInfo({ data, setData, cats, onPrev, onNext }) {
  return (
    <StepLayout step={1} title="기본 정보" sub="몇 가지 간단한 키워드 태그로 당신에게 Fin. 한 상품을 찾아드립니다.">
      <div className="flex items-center gap-2 mb-2">
        <p className="text-md text-[#454545] font-semibold mb-0">현재 신분</p>
        <div className="w-3 h-3 rounded-full border border-[#03BFA5] text-[#03BFA5] flex items-center justify-center text-[10px] font-bold">!</div>
      </div>
      <div className="flex flex-wrap gap-2 mb-5">
        {cats.status.map((s) => (
          <Tag key={s.optionId} label={s.optionValue}
            selected={(data.status || []).includes(s.optionId)}
            onClick={() => toggleField(data, setData, "status", s.optionId)} />
        ))}
      </div>

      <div className="flex items-center gap-2 mb-2">
        <p className="text-md text-[#454545] font-semibold mb-0">희망 저축 기간</p>
        <div className="w-3 h-3 rounded-full border border-[#03BFA5] text-[#03BFA5] flex items-center justify-center text-[10px] font-bold">!</div>
      </div>
      <div className="flex flex-wrap gap-2">
        {cats.savingPeriod.map((p) => (
          <Tag key={p.optionId} label={p.optionValue}
            selected={(data.savingPeriod || []).includes(p.optionId)}
            onClick={() => toggleField(data, setData, "savingPeriod", p.optionId)}/>
        ))}
      </div>
      <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.savingPeriod || data.savingPeriod.length === 0} />
    </StepLayout>
  );
}

// 3. 핵심 혜택 + 은행 거래 (Step 1-3)
export function StepBenefits({ data, setData, cats, onPrev, onNext }) {
  return (
    <StepLayout step={1} title="기본 정보" sub="몇 가지 간단한 키워드 태그로 당신에게 Fin. 한 상품을 찾아드립니다.">
      <div className="flex items-center gap-2 mb-2">
        <p className="text-md text-[#454545] font-semibold mb-0">핵심 혜택</p>
        <div className="w-3 h-3 rounded-full border border-[#03BFA5] text-[#03BFA5] flex items-center justify-center text-[10px] font-bold">!</div>
      </div>
      <div className="flex flex-wrap gap-2 mb-5">
        {cats.benefits.map((b) => (
          <Tag key={b.optionId} label={b.optionValue}
            selected={(data.benefits || []).includes(b.optionId)}
            onClick={() => toggleField(data, setData, "benefits", b.optionId)} />
        ))}
      </div>

      <div className="flex items-center gap-2 mb-2">
        <p className="text-md text-[#454545] font-semibold mb-0">은행 거래</p>
        <div className="w-3 h-3 rounded-full border border-[#03BFA5] text-[#03BFA5] flex items-center justify-center text-[10px] font-bold">!</div>
      </div>
      <div className="flex flex-wrap gap-2">
        {cats.bankRelation.map((b) => (
          <Tag key={b.optionId} label={b.optionValue}
            selected={(data.bankRelation || []).includes(b.optionId)}
            onClick={() => toggleField(data, setData, "bankRelation", b.optionId)} />
        ))}
      </div>
      <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.benefits || data.benefits.length === 0} />
    </StepLayout>
  );
}

const YEARS  = Array.from({ length: 50 }, (_, i) => 1975 + i);
const MONTHS = Array.from({ length: 12 }, (_, i) => i + 1);
const DAYS   = Array.from({ length: 31 }, (_, i) => i + 1);

export function StepPersonalInfo({ data, setData, onPrev, onNext }) {
  return (
    <StepLayout step={2} title="상세 정보" sub="Y-Fin.만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다.">
      
      <p className="text-md text-[#454545] font-semibold mb-3">개인 기본 정보</p>

      {/* 생년월일 섹션 */}
      <div className="pl-4">
        <div className="flex items-center gap-2 mb-2">
          <p className="text-[15px] text-[#454545] font-normal mb-0">생년월일</p>
          <div className="w-4 h-4 rounded-full border border-[#03BFA5] text-[#03BFA5] flex items-center justify-center text-[10px] font-bold">i</div>
        </div>
        
        <div className="flex items-center gap-2 mb-2 flex-wrap">
          {[
            { key: "birthYear",  items: YEARS,  unit: "년" },
            { key: "birthMonth", items: MONTHS, unit: "월" },
            { key: "birthDay",   items: DAYS,   unit: "일" },
          ].map(({ key, items, unit }) => (
            <div key={key} className="flex items-center gap-1">
              <FormSelect 
                value={data[key] || ""} 
                onChange={(e) => setData({ ...data, [key]: e.target.value })}
                className="w-21.25 h-9.5 px-2 text-sm border-[#D9D9D9] rounded-md focus:border-[#03BFA5]"
              >
                <option value="">선택</option>
                {items.map((v) => <option key={v}>{v}</option>)}
              </FormSelect>
              <span className="text-[14px] text-[#454545] mr-1">{unit}</span>
            </div>
          ))}
          
          <div className="flex-1 min-w-70">
            <InfoBox>기본 만 19~34세, 군필자는 39세까지 선택 가능</InfoBox>
          </div>
        </div>

        <div className="h-4"></div>

        {/* 개인 연소득 섹션 */}
        <div className="flex items-center gap-2 mb-2">
          <p className="text-[15px] text-[#454545] font-normal mb-0">개인 연소득</p>
          <div className="w-4 h-4 rounded-full border border-[#03BFA5] text-[#03BFA5] flex items-center justify-center text-[10px] font-bold">i</div>
          </div>
          
        <div className="flex flex-col gap-3 mb-8">
          <div className="flex gap-2 items-center flex-wrap">
            
            <div className="flex h-9 items-center border border-[#D9D9D9] rounded-md px-3 py-2 bg-white sm:flex-none sm:w-64">
              <FormInput type="number" placeholder="숫자(단위:만원)를 입력하세요."
                value={data.income || ""}
                onChange={(e) => setData({ ...data, income: e.target.value })}
                className="w-full border-none outline-none focus:ring-0 text-sm p-0 m-0 bg-transparent" />
            </div>

            <button type="button" className="h-9 text-sm text-[#03BFA5] bg-white border border-[#03BFA5] rounded-lg px-4 py-2 flex items-center justify-center font-medium hover:bg-teal-50/40 transition-all">만원
            </button>
            <InfoBox>미취업자는 0원 입력 기본값(수정 가능) / 최대 1억원 입력 가능</InfoBox>
          </div>
        </div>
      </div>
      <div className="mt-8">
        <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.income}/>
      </div>
    </StepLayout>
  );
}

// 5. 거주지역 (Step 2-2)
export function StepRegion({ data, setData, cats, onNext, onPrev }) {
  return (
    <StepLayout step={2} title="상세정보" sub="Y-Fin만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다.">
      <p className="text-[18px] text-[#454545] font-semibold mb-1.5">거주지역</p>
      <FormSelect
        value={data.region || ""}
        onChange={(e) => setData({ ...data, region: e.target.value })}
        className="w-110"
      >
        <option value="">선택해주세요.</option>
        {cats.regions.map((r) => (
          <option key={r.optionId} value={r.optionId}>{r.optionValue}</option>
        ))}
      </FormSelect>
      <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.region} />
    </StepLayout>
  );
}

// 6. 가구정보 (가구원 수, 가구 소득) (Step 2-3)
export function StepHouseholdIncome({ data, setData, cats, onPrev, onNext }) {
  const count = data.householdCount || 1;
  return (
    <StepLayout step={2} title="상세 정보" sub="Y-Fin.만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다.">
      <p className="text-[18px] text-[#454545] font-semibold mb-3">가구 정보</p>

      <div className="pl-4">
        {/*가구원 수*/}
        <p className="text-[15px] text-[#454545] font-medium mb-3">가구원 수</p>
        <div className="flex items-center w-fit h-10 border border-[#D9D9D9] rounded-full px-2 mb-8 bg-white">
          <button 
            type="button"
            onClick={() => setData({ ...data, householdCount: Math.max(1, count + 1) })}
            className="w-8 h-8 flex items-center justify-center text-[#454545] text-lg"
          >+</button>
          <span className="text-[15px] font-semibold px-4 min-w-15 text-center">
            {count}인
          </span>
          <button 
            type="button"
            onClick={() => setData({ ...data, householdCount: Math.max(1, count - 1) })}
            className="w-8 h-8 flex items-center justify-center text-[#454545] text-lg">-</button>
        </div>

        {/*가구 소득*/}
        <div className="flex items-center gap-1.5 mb-4">
          <p className="text-[15px] text-[#454545] font-medium">가구 소득</p>
          <div className="w-3.75 h-3.75 rounded-full border border-[#03BFA5] flex items-center justify-center">
            <span className="text-[#03BFA5] text-[10px] font-bold">i</span>
          </div>
        </div>

        <div className="flex flex-col gap-2 mb-6 max-w-105">
          {cats.incomeLevel.map((item) => {
            const isSelected = data.incomeLevel === item.label;
            return (
              <label 
                key={item.label}
                className={`flex items-center gap-3 px-4 py-2.5 rounded-full border cursor-pointer transition-all ${
                  isSelected
                    ? "border-[#03BFA5] bg-[#03BFA5] text-white"
                    : "border-[#D9D9D9] bg-white hover:border-[#03BFA5]"
                }`}
              >
                <input type="checkbox" 
                  className="hidden"
                  checked={isSelected}
                  onChange={() => setData({ ...data, incomeLevel: isSelected ? "" : item.label })} 
                />
                
                <div className={`w-5 h-5 rounded-md flex items-center justify-center transition-all ${
                  isSelected ? "bg-white" : "border border-gray-300 bg-white"
                }`}>
                  {isSelected && (
                    <svg className="w-3.5 h-3.5 text-[#03BFA5]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={4} d="M5 13l4 4L19 7" />
                    </svg>
                  )}
                </div>

                <span className={`text-[14px] flex-1 ${isSelected ? "text-white" : "text-[#454545]"}`}>
                  {item.label}
                </span>
                {item.amount && (
                  <span className={`text-[14px] font-medium ${isSelected ? "text-[#EFFFFD]" : "text-[#03BFA5]"}`}>
                    {item.amount}
                  </span>
                )}
              </label>
            );
          })}
        </div>
        <InfoBox>가구원 수 변경 시 중위소득에 해당하는 금액이 자동 조정됩니다.</InfoBox>
      </div>
      <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.incomeLevel}/>
    </StepLayout>
  );
}

// 7. 가구정보 (무주택 여부) (Step 2-4)
export function StepHousing({ data, setData, onPrev, onNext }) {
  return (
    <StepLayout 
      step={2} 
      title="상세 정보" 
      sub="Y-Fin.만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다."
    >
      <div className="flex items-center gap-1.5 mb-6">
        <p className="text-[18px] text-[#454545] font-bold">가구 정보</p>
      </div>

      <div className="pl-4">
        {/*무주택 여부*/}
        <div className="flex items-center gap-1.5 mb-4">
          <p className="text-[15px] text-[#454545] font-medium">무주택 여부</p>
          <div className="w-[15px] h-[15px] rounded-full border border-[#03BFA5] flex items-center justify-center">
            <span className="text-[#03BFA5] text-[10px] font-bold">i</span>
          </div>
        </div>

        <div className="flex gap-3 mb-4">
          {["무주택", "유주택"].map((opt) => {
            const isSelected = data.housingStatus === opt;
            return (
              <label 
                key={opt}
                className={`flex items-center gap-3 px-6 py-2.5 rounded-full border cursor-pointer transition-all min-w-[140px] justify-center ${
                  isSelected 
                    ? "border-[#03BFA5] bg-[#03BFA5] text-white" 
                    : "border-[#D9D9D9] bg-white text-[#454545] hover:border-[#03BFA5]"
                }`}
              >
                <input type="checkbox" 
                  className="hidden" 
                  checked={isSelected}
                  onChange={() => setData({ ...data, housingStatus: opt })} />
                
                <div className={`w-5 h-5 rounded-md flex items-center justify-center transition-all ${
                  isSelected ? "bg-white" : "border border-gray-300 bg-white"}`}>
                  {isSelected && (
                    <svg className="w-3.5 h-3.5 text-[#03BFA5]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={4} d="M5 13l4 4L19 7" />
                    </svg>
                  )}
                </div>
                <span className="text-[15px] font-medium">{opt}</span>
              </label>
            );
          })}
        </div>

        {/*세대주 여부*/}
        <label 
          className={`flex items-center gap-3 px-8 py-3 rounded-full border transition-all mb-6 cursor-pointer w-full max-w-[340px] ${
            data.isTenant ? "bg-[#03BFA5] border-[#03BFA5] text-white" : "bg-white border-[#D9D9D9] text-[#454545] hover:border-[#03BFA5]"
          }`}>
          <input type="checkbox" 
            className="hidden"
            checked={data.isTenant || false}
            onChange={(e) => setData({ ...data, isTenant: e.target.checked })} 
          />
          <div className={`w-5 h-5 rounded-md flex items-center justify-center transition-all ${
            data.isTenant ? "bg-white" : "border border-gray-300 bg-white"
          }`}>
            {data.isTenant && (
              <svg className="w-3.5 h-3.5 text-[#03BFA5]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={4} d="M5 13l4 4L19 7" />
              </svg>
            )}
          </div>
          <span className="text-[15px] font-medium">세대주입니다.</span>
        </label>
        <div className="max-w-[420px]">
          <InfoBox>기본 만 19~34세, 군필자는 39세까지 선택 가능</InfoBox>
        </div>
      </div>

      <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.housingStatus} />
    </StepLayout>
  );
}

// 8. 재직 정보 (근속 기간) (Step 2-5)
export function StepEmployment({ data, setData, onPrev, onSubmit }) {
  const months = data.employmentMonths || 0;
  
  return (
    <StepLayout 
      step={2} 
      title="상세 정보" 
      sub="Y-Fin.만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다."
    >
      <div className="flex items-center gap-1.5 mb-6">
        <p className="text-[18px] text-[#454545] font-bold">재직 정보</p>
      </div>

      <div className="pl-4">
        {/* 근속 기간 */}
        <div className="flex items-center gap-1.5 mb-6">
          <p className="text-[15px] text-[#454545] font-medium">근속 기간</p>
          <div className="w-[15px] h-[15px] rounded-full border border-[#03BFA5] flex items-center justify-center">
            <span className="text-[#03BFA5] text-[10px] font-bold">i</span>
          </div>
        </div>

        <div className="w-full max-w-[450px] mb-2">
          <input 
            type="range" 
            min={0} 
            max={120} 
            value={months}
            onChange={(e) => setData({ ...data, employmentMonths: Number(e.target.value) })}
            className="w-full h-[4px] bg-[#E5E5E5] rounded-lg appearance-none cursor-pointer accent-[#03BFA5]" 
          />
          <div className="flex justify-between text-[13px] text-[#8F8F8F] mt-2">
            <span>0개월</span>
            <span>120개월</span>
          </div>
        </div>

        <div className="flex items-center w-full max-w-[340px] h-[40px] border border-[#D9D9D9] rounded-full px-5 bg-white mb-3 mt-8">
          <FormInput 
            type="number" 
            value={months}
            onChange={(e) => setData({ ...data, employmentMonths: Math.min(120, Math.max(0, Number(e.target.value))) })}
            className="w-18 text-center text-[#A5A5A5] text-[15px] font-medium border-none focus:ring-0" 
          />
          <span className="text-[15px] text-[#454545] ml-2">개월</span>
        </div>
        <label 
          className={`flex items-center gap-3 px-6 py-2 rounded-full border transition-all mb-4 cursor-pointer w-full max-w-[340px] ${
            data.isFirstJob ? "bg-[#03BFA5] border-[#03BFA5]" : "bg-white border-[#D9D9D9] hover:border-[#03BFA5]"
          }`}
        >
          <input 
            type="checkbox" 
            className="hidden" 
            checked={data.isFirstJob || false}
            onChange={(e) => setData({ ...data, isFirstJob: e.target.checked })} 
          />
          
          <div className={`w-5 h-5 rounded-md flex items-center justify-center transition-all ${
            data.isFirstJob ? "bg-white" : "border border-gray-300 bg-white"
          }`}>
            {data.isFirstJob && (
              <svg className="w-3.5 h-3.5 text-[#03BFA5]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={4} d="M5 13l4 4L19 7" />
              </svg>
            )}
          </div>

          <span className={`text-[15px] font-medium ${data.isFirstJob ? "text-white" : "text-[#454545]"}`}>
            첫 직장입니다.
          </span>
        </label>
        <div className="max-w-[450px]">
          <InfoBox>첫 직장 선택 시 신규 취업자 전용 상품을 추천합니다.</InfoBox>
        </div>
      </div>
      <div className="mt-12">
        <NavButtons onPrev={onPrev} onNext={onSubmit} isLast />
      </div>
    </StepLayout>
  );
}