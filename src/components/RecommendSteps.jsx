import StepLayout from "./StepLayout";
import NavButtons from "./NavButtons";
import InfoBox from "./InfoBox";
import Tag from "./Tag";
import  InfoIcon  from "./InfoIcon";
import { FormInput, FormSelect } from "./FormFields";
import { toggleField } from "../utils/toggleField";
import { useState } from "react";

// 1. 저축 계획 (Step 1-1)
export function StepSavingPlan({ data, setData, cats, onPrev, onNext }) {
  const amount = data.monthlyAmount || 1;
  return (
    <div className = "pl-5">
    <StepLayout step={1} title="기본 정보" sub= "월 납입 희망액을 슬라이더로 조정하거나 직접 입력해주세요">
      <div className="pl-5">
        <div className="flex items-center gap-1.5 mb-4">
          <p className="text-[18px] font-semibold text-[#454545]">월 납입 희망액</p>
          <InfoIcon text="예상 만기 수령액 및 적합도 계산에 활용됩니다."/>
        </div>

        <div className="w-full max-w-112.5">
          <input type="range" min={1} max={100} value={amount}
            onChange={(e) => setData({ ...data, monthlyAmount: Number(e.target.value) })}
            className="w-110 h-1 bg-[#D0D0D0] rounded-lg appearance-none cursor-pointer accent-[#03BFA5] mb-1" />
          
          <div className="flex font-medium justify-between text-[13px] text-[#454545] mb-10 px-0.5">
            <span>1만원</span>
            <span>100만원</span>
          </div>
        </div>

        <div className="flex items-center border border-[#CACACA] rounded-full w-[120px] h-[30px] px-3 mb-16 bg-white">
          <div className="flex items-center justify-center border border-[#CACACA] rounded-[2px] w-[50px] h-[15px] ml-1 mr-2">
            <input type="number" value={amount}
              onChange={(e) => setData({ ...data, monthlyAmount: Math.min(100, Math.max(1, Number(e.target.value))) })}
              className="w-full text-center text-[#CACACA] text-[13px] font-regular bg-transparent outline-none ml-3" 
            />
          </div>
          <span className="text-[14px] font-regular text-[#454545]">만원</span>
        </div>
      </div>
      <NavButtons isFirst onPrev={onPrev} onNext={onNext} />
    </StepLayout>
    </div>
  );
}

// 2. 현재 신분 + 희망 저축 기간 (Step 1-2)
export function StepBasicInfo({ data, setData, cats, onPrev, onNext }) {
  return (
    <div className = "pl-5">
    <StepLayout step={1} title="기본 정보" sub="몇 가지 간단한 키워드 태그로 당신에게 Fin. 한 상품을 찾아드립니다.">
      <div className="flex items-center gap-2 mb-3">
        <p className="text-[18px] text-[#454545] font-semibold mb-0">현재 신분</p>
        <InfoIcon text = "가입 자격 1순위로, 미취업/재직/군복무에 따라 추천 가능 상품군이 달라집니다."/>
      </div>
      <div className="flex flex-wrap gap-2 mb-5">
        {cats.status.map((s) => (
          <Tag key={s.optionId} label={s.optionValue}
            selected={(data.status || []).includes(s.optionId)}
            onClick={() => toggleField(data, setData, "status", s.optionId)} />
        ))}
      </div>

      <div className="flex items-center gap-2 mb-3">
        <p className="text-[18px] text-[#454545] font-semibold mb-0">희망 저축 기간</p>
        <InfoIcon text = "목표 저축 금액에 맞는 상품을 추천해드립니다."/>
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
    </div>
  );
}

// 3. 핵심 혜택 + 은행 거래 (Step 1-3)
export function StepBenefits({ data, setData, cats, onPrev, onNext }) {
  return (
    <div className = "pl-5">
    <StepLayout step={1} title="기본 정보" sub="몇 가지 간단한 키워드 태그로 당신에게 Fin. 한 상품을 찾아드립니다.">
      <div className="flex items-center gap-2 mb-3">
        <p className="text-[18px] text-[#454545] font-semibold mb-0">핵심 혜택</p>
        <InfoIcon text={`선호하는 혜택을 선택하면 우선순위에 반영됩니다.
(복수 선택 가능)`} />
      </div>
      <div className="flex flex-wrap gap-2 mb-5">
        {cats.benefits.map((b) => (
          <Tag key={b.optionId} label={b.optionValue}
            selected={(data.benefits || []).includes(b.optionId)}
            onClick={() => toggleField(data, setData, "benefits", b.optionId)} />
        ))}
      </div>

      <div className="flex items-center gap-2 mb-3">
        <p className="text-[18px] text-[#454545] font-semibold mb-0">은행 거래</p>
        <InfoIcon text={`우대 금리 조건 확인을 위한 선택 항목입니다.
(복수 선택 가능)`} />
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
    </div>
  );
}

const YEARS  = Array.from({ length: 50 }, (_, i) => 1975 + i);
const MONTHS = Array.from({ length: 12 }, (_, i) => i + 1);
const DAYS   = Array.from({ length: 31 }, (_, i) => i + 1);

export function StepPersonalInfo({ data, setData, onPrev, onNext }) {
  return (
    <div className = "pl-5">
    <StepLayout step={2} title="상세 정보" sub="Y-Fin.만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다.">
      
      <p className="text-[18px] text-[#454545] font-semibold mb-3">개인 기본 정보</p>

      {/* 생년월일 섹션 */}
      <div className="pl-4">
        <div className="flex items-center gap-2 mb-2">
          <p className="text-[16px] text-[#454545] font-normal mb-0">생년월일</p>
        <InfoIcon text={`법정 연령 요건은 가입의 첫 관문으로,
군필자의 경우 복무 기간만큼 상한 연령이 확대됩니다.`} />
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
                className="w-21.25 h-8.5 px-2 text-sm border-[#D9D9D9] rounded-sm focus:border-[#03BFA5]"
              >
                <option value="">선택</option>
                {items.map((v) => <option key={v}>{v}</option>)}
              </FormSelect>
              <span className="text-[14px] text-[#454545] mr-1">{unit}</span>
            </div>
          ))}
          
          <div className="flex-1 min-w-70">
            <InfoBox type = "mint-pill">기본 만 19~34세, 군필자는 39세까지 선택 가능</InfoBox>
          </div>
        </div>

        <div className="h-4"></div>

        {/* 개인 연소득 섹션 */}
        <div className="flex items-center gap-2 mb-3">
          <p className="text-[16px] text-[#454545] font-normal mb-0">개인 연소득</p>
          <InfoIcon text = "기여금 매칭 비율과 가입 가능 여부를 판단합니다."/>
          </div>
          
        <div className="flex flex-col gap-3 mb-8">
          <div className="flex items-center flex-wrap gap-4">
            <div className = "flex items-center">
              <div className="flex h-8.5 items-center border border-[#D9D9D9] rounded-sm px-3 py-2 bg-white sm:flex-none sm:w-64">
                <FormInput type="number" placeholder="숫자(단위:만원)를 입력하세요."
                  value={data.income || ""}
                  onChange={(e) => {
                      let val = e.target.value.replace(/[^0-9]/g, '');
                      // 0 ~ 10000 범위 제한 적용
                      if (val !== "") {
                        let num = parseInt(val, 10);
                        if (num > 10000) val = "10000";
                        else val = num.toString(); 
                      }
                      setData({ ...data, income: val });
                    }}
                  className="w-full border-none outline-none focus:ring-0 text-sm p-0 m-0 bg-transparent" />
              </div>
                <div className="h-8.5 px-3 border border-[#03BFA5] bg-white rounded-sm flex items-center justify-center mr-1">
                  <span className="text-[14px] text-[#03BFA5] font-medium">만원</span>
                </div>
              </div>
              <div className="flex-1 min-w-70">
                <InfoBox type="mint-rect">미취업자는 0원 입력 기본값(수정 가능) / 최대 1억원 입력 가능</InfoBox>
              </div>
          </div>
        </div>
      </div>
      <div className="mt-8">
        <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.income}/>
      </div>
    </StepLayout>
    </div>
  );
}

// 5. 거주지역 (Step 2-2)
export function StepRegion({ data, setData, cats, onNext, onPrev }) {
  return (
    <div className = "pl-5">
    <StepLayout step={2} title="상세정보" sub="Y-Fin만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다.">
      <div className="flex items-center gap-1.5 mb-3">
        <p className="text-[18px] text-[#454545] font-semibold mb-1.5">거주지역</p>
        <InfoIcon text={`지자체별 청년 금융상품(서울 희망두배, 부산 기쁨 
두배 등) 필터링에 활용됩니다.`} />
      </div>
      <FormSelect
        value={data.region || ""}
        onChange={(e) => setData({ ...data, region: e.target.value })}
        className="w-90"
      >
        <option value="">선택해주세요.</option>
        {cats.regions.map((r) => (
          <option key={r.optionId} value={r.optionId}>{r.optionValue}</option>
        ))}
      </FormSelect>
      <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.region} />
    </StepLayout>
    </div>
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
        <div className="flex items-center gap-1.5 mb-3">
          <p className="text-[15px] text-[#454545] font-medium">가구 소득</p>
          <InfoIcon text = "가구원 수는 중위소득 산정 기준입니다."/>
        </div>
        <div className="flex items-center w-fit h-10 border border-[#D9D9D9] rounded-full px-2 mb-6 bg-white">
          <button 
            type="button"
            onClick={() => setData({ ...data, householdCount: Math.max(1, count + 1) })}
            className="w-7 h-4.5 flex items-center justify-center border border-[#454545] rounded-full text-[#454545] text-[15px]">+</button>
          <span className="text-[18px] font-regular px-4 min-w-15 text-center">
            {count}인
          </span>
          <button 
            type="button"
            onClick={() => setData({ ...data, householdCount: Math.max(1, count - 1) })}
            className="w-7 h-4.5 flex items-center justify-center border border-[#454545] rounded-full text-[#454545] text-[15px]">-</button>
        </div>

        {/*가구 소득*/}
        <div className="flex items-center gap-1.5 mb-3">
          <p className="text-[15px] text-[#454545] font-medium">가구 소득</p>
          <InfoIcon text="가구원 수에 따른 중위소득 기준으로 선택해주세요." />
        </div>

        <div className="flex flex-col gap-1 mb-6 max-w-100">
          {cats.incomeLevel.map((item) => {
            const isSelected = data.incomeLevel === item.label;
            return (
              <label 
                key={item.label}
                className={`flex items-center gap-2 px-4 py-2 rounded-full border cursor-pointer transition-all ${
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
                
                <div className={`w-4 h-4 rounded-sm flex items-center justify-center transition-all ${
                  isSelected ? "bg-white" : "border border-[#454545] bg-white"
                }`}>
                  {isSelected && (
                    <svg className="w-3.5 h-3.5 text-[#03BFA5]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={4} d="M5 13l4 4L19 7" />
                    </svg>
                  )}
                </div>

                <span className={`text-[15px] font-regular w-[110px] ${isSelected ? "text-white" : "text-[#454545]"}`}>
                  {item.label}
                </span>
                {item.amount && (
                  <span className={`text-[14px] font-regular ${isSelected ? "text-[#EFFFFD]" : "text-[#03BFA5]"}`}>
                    {item.amount}
                  </span>
                )}
              </label>
            );
          })}
        </div>
            <div className="flex items-center gap-2 bg-[#F6FCFA] border border-[#03BFA5] flex-2 rounded-4xl px-5 py-2 h-8.5 text-xs text-[#03BFA5] w-full max-w-100">
            <div className="w-4 h-4 rounded-full bg-[#03BFA5] text-[#FFFFFF] flex items-center justify-center text-[10px] font-bold shrink-0">i</div>
            <span className="text-[#000000] text-xs bg-[#EFFFFD] font-regular">가구원 수 변경 시 중위소득에 해당하는 금액이 자동 조정됩니다.</span>
          </div>
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
      <div className= "pl-5">
      <div className="flex items-center gap-1.5 mb-4">
        <p className="text-[18px] text-[#454545] font-bold">가구 정보</p>
      </div>

      <div className="pl-4">
        {/*무주택 여부*/}
        <div className="flex items-center gap-1.5 mb-3">
          <p className="text-[15px] text-[#454545] font-medium">무주택 여부</p>
          <InfoIcon text = "청약/주거 지원 상품 자격 요건입니다."/>
        </div>

        <div className="flex gap-3 mb-4">
          {["무주택", "유주택"].map((opt) => {
            const isSelected = data.housingStatus === opt;
            return (
              <label 
                key={opt}
                className={`flex items-center gap-2 px-6 py-1.5 rounded-full border cursor-pointer transition-all min-w-[140px] justify-center ${
                  isSelected 
                    ? "border-[#03BFA5] bg-[#03BFA5] text-white" 
                    : "border-[#D9D9D9] bg-white text-[#454545] hover:border-[#03BFA5]"
                }`}
              >
                <input type="checkbox" 
                  className="hidden" 
                  checked={isSelected}
                  onChange={() => setData({ ...data, housingStatus: opt })} />
                
                <div className={`w-4 h-4 rounded-xs flex items-center justify-center transition-all ${
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
          className={`flex items-center gap-2 px-8 py-1.5 rounded-full border transition-all mb-6 cursor-pointer w-full max-w-[300px] ${
            data.isTenant ? "bg-[#03BFA5] border-[#03BFA5] text-white" : "bg-white border-[#D9D9D9] text-[#454545] hover:border-[#03BFA5]"
          }`}>
          <input type="checkbox" 
            className="hidden"
            checked={data.isTenant || false}
            onChange={(e) => setData({ ...data, isTenant: e.target.checked })} 
          />
          <div className={`w-4 h-4 rounded-xs flex items-center justify-center transition-all ${
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
            <div className="flex items-center gap-2 bg-[#F6FCFA] border border-[#03BFA5] flex-2 rounded-4xl px-5 py-2 h-8.5 text-xs text-[#03BFA5] w-full max-w-75">
            <div className="w-4 h-4 rounded-full bg-[#03BFA5] text-[#FFFFFF] flex items-center justify-center text-[10px] font-bold shrink-0">i</div>
            <span className="text-[#000000] text-xs bg-[#EFFFFD] font-regular">기본 만 19~34세, 군필자는 39세까지 선택 가능</span>
        </div>    
      </div>
      </div>
      <NavButtons onPrev={onPrev} onNext={onNext} disabled={!data.housingStatus} />
    </StepLayout>
  );
}

// 8. 재직 정보 (근속 기간) (Step 2-5)
export function StepEmployment({ data, setData, onPrev, onNext }) {
  const months = data.employmentMonths || 0;
  
  return (
    <StepLayout 
      step={2} 
      title="상세 정보" 
      sub="Y-Fin.만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다."
    >
      <div className="flex items-center gap-1.5 mb-4">
        <p className="text-[18px] text-[#454545] font-bold">재직 정보</p>
      </div>

      <div className="pl-4">
        {/* 근속 기간 */}
        <div className="flex items-center gap-1.5 mb-4">
          <p className="text-[17px] text-[#454545] font-medium">근속 기간</p>
          <InfoIcon text={`일부 상품은 근속 요건이 있습니다.
(예: 내일 채움 공제 6개월 이상)`} />
        </div>

        <div className="w-full max-w-112.5 mb-1">
          <input 
            type="range" min={0} max={120} value={months}
            onChange={(e) => setData({ ...data, employmentMonths: Number(e.target.value) })}
            className="w-full h-1 bg-[#E5E5E5] rounded-lg appearance-none cursor-pointer accent-[#03BFA5]" 
          />
          <div className="flex justify-between text-[14px] text-[#454545] mt-2">
            <span>0개월</span>
            <span>120개월</span>
          </div>
        </div>


      <div className="flex items-center border border-[#D9D9D9] rounded-full w-full max-w-[340px] h-8 px-5 bg-white mb-3">
        
        <div className="flex items-center justify-center border border-[#E0E0E0] rounded-xs w-15 h-5 mr-3">
          <FormInput 
            type="text" 
            value={months}
            onChange={(e) => setData({ ...data, employmentMonths: Math.min(120, Math.max(0, Number(e.target.value))) })}
            className="w-full text-center text-[#CACACA] text-[15px] font-medium bg-transparent outline-none border-none p-0 m-0 focus:ring-0 focus:text-[#454545]" 
          />
        </div>

        {/* 단위 텍스트 */}
        <span className="text-[16px] font-regular text-[#454545]">개월</span>
      </div>
        <label 
          className={`h-8 flex items-center gap-3 px-6 py-2 rounded-full border transition-all mb-4 cursor-pointer w-full max-w-[340px] ${
            data.isFirstJob ? "bg-[#03BFA5] border-[#03BFA5]" : "bg-white border-[#D9D9D9] hover:border-[#03BFA5]"
          }`}
        >
          <input 
            type="checkbox" 
            className="hidden" 
            checked={data.isFirstJob || false}
            onChange={(e) => setData({ ...data, isFirstJob: e.target.checked })} 
          />
          <div className={`w-4 h-4 rounded-xs flex items-center justify-center transition-all ${
            data.isFirstJob ? "bg-white" : "border border-gray-300 bg-white"
          }`}>
            {data.isFirstJob && (
              <svg className="w-3.5 h-3.5 text-[#03BFA5]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={4} d="M5 13l4 4L19 7" />
              </svg>
            )}
          </div>
          
          <span className={`text-[16px] font-regular ${data.isFirstJob ? "text-white" : "text-[#454545]"}`}>
            첫 직장입니다.
          </span>
        </label>
          <div className="flex items-center gap-2 bg-[#F6FCFA] border border-[#03BFA5] flex-2 rounded-4xl px-5 py-2 h-8.5 text-xs text-[#03BFA5] w-full max-w-80">
          <div className="w-4 h-4 rounded-full bg-[#03BFA5] text-[#FFFFFF] flex items-center justify-center text-[10px] font-bold shrink-0">i</div>
          <span className="text-[#000000] text-xs bg-[#EFFFFD] font-regular">첫 직장 선택 시 신규 취업자 전용 상품을 추천합니다.</span>
      </div>
      <div className="mt-12">
        <NavButtons onPrev={onPrev} onNext={onNext} />
      </div>
      </div>
    </StepLayout>
  );
}

const TABS = [
  { id: '전체', label: '전체', icon: 'M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z' },
  { id: '시중', label: '시중', icon: 'M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z' },
  { id: '인터넷', label: '인터넷', icon: 'M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z' },
  { id: '특수', label: '특수', icon: 'M3 21v-4m0 0V5a2 2 0 012-2h6.5l1 1H21l-3 6 3 6h-8.5l-1-1H5a2 2 0 00-2 2zm9-13.5V9' },
  { id: '지방', label: '지방', icon: 'M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z' }
];

// "" 부분은 백엔드에서 주는 거주지역 optionId
const REGION_BANK_MAP = {
  "reg_05": "BNK부산",       
  "reg_06": "BNK경남은행",
  "reg_07": "광주은행",
  "reg_08": "전북은행",
  "reg_09": "제주은행"
};

function BankSelector({ 
  theme = 'mint', 
  title, 
  tagText, 
  infoText,
  icontext,
  selectedBanks = [], 
  onChange, 
  disabledBanks = [],
  cats,
  userRegion 
}) {
  const [activeTab, setActiveTab] = useState('전체');

  // 테마별 색상 매핑
  const colors = {
    mint: { main: '#03BFA5', bg: '#F2FBF9', text: 'text-[#03BFA5]', border: 'border-[#03BFA5]' },
    blue: { main: '#4A90E2', bg: '#F0F6FF', text: 'text-[#4A90E2]', border: 'border-[#4A90E2]' }
  };
  const infoBoxColors = theme === 'mint' ? {
    wrapper: "bg-[#FFFFFF] border-[#03BFA5]", 
    icon: "bg-[#03BFA5] text-[#FFFFFF]",       
    text: "text-[#03BFA5] bg-transparent"      
  } : {
    wrapper: "bg-[#FFFFFF] border-[#4A90E2]",
    icon: "bg-[#4A90E2] text-[#FFFFFF]",
    text: "text-[#4A90E2] bg-transparent"
  };
  const themeColor = colors[theme];

  const categoriesToUse = cats?.bankCategories || [];

  const displayedCategories = categoriesToUse.map(category => {
    if (category.id === '지방') {
      if (activeTab === '전체') {
        const myLocalBank = REGION_BANK_MAP[userRegion];
        return { ...category, banks: category.banks.filter(b => b === myLocalBank) };
      }
      return category;
    }
    return category;
  }).filter(category => {
    if (activeTab !== '전체' && category.id !== activeTab) return false;
    if (category.banks.length === 0) return false;
    return true;
  });

  const toggleBank = (bank) => {
    if (disabledBanks.includes(bank)) return; 
    if (selectedBanks.includes(bank)) {
      onChange(selectedBanks.filter(b => b !== bank));
    } else {
      onChange([...selectedBanks, bank]);
    }
  };

  const selectAll = (categoryBanks) => {
    const validBanks = categoryBanks.filter(b => !disabledBanks.includes(b));
    const allSelected = validBanks.length > 0 && validBanks.every(b => selectedBanks.includes(b));
    
    if (allSelected) {
      onChange(selectedBanks.filter(b => !validBanks.includes(b)));
    } else {
      const newSelection = new Set([...selectedBanks, ...validBanks]);
      onChange(Array.from(newSelection));
    }
  };

  return (
    <div className="w-full mb-10">
      <div className="flex items-center gap-2 mb-4">
        <svg className={`w-5 h-5 ${themeColor.text}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z" />
        </svg>
        <span className="text-[16px] font-bold text-[#333333]">{title}</span>
        <span className={`text-[11px] font-medium px-2 py-0.5 rounded-full bg-opacity-10 ${themeColor.text}`} style={{ backgroundColor: themeColor.bg }}>
          {tagText}
        </span>
        <InfoIcon text={icontext}/>
      </div>

      <div className="flex gap-2 mb-6">
        {TABS.map(tab => {
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md border text-[13px] transition-all ${
                isActive 
                  ? `${themeColor.border} ${themeColor.text} font-bold` 
                  : 'border-[#D9D9D9] text-[#7A7A7A] hover:border-[#A5A5A5]'
              }`}
              style={{ backgroundColor: isActive ? themeColor.bg : '#FFFFFF' }}
            >
              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={tab.icon} /></svg>
              {tab.label}
            </button>
          );
        })}
      </div>

      <div className="flex flex-col gap-6 mb-6">
        {displayedCategories.map(category => (
          <div key={category.id}>
            <div className="flex items-center justify-between border-b border-[#E0E0E0] pb-2 mb-3">
              <span className="text-[13px] text-[#7A7A7A]">{category.title}</span>
              <button onClick={() => selectAll(category.banks)} className={`text-[12px] ${themeColor.text}`}>전체선택</button>
            </div>
            
            <div className="grid grid-cols-3 gap-3">
              {category.banks.map(bank => {
                const isSelected = selectedBanks.includes(bank);
                const isDisabled = disabledBanks.includes(bank);
                
                let btnStyle = "border-[#D9D9D9] text-[#454545] bg-white hover:border-[#A5A5A5]";
                if (isDisabled) {
                  btnStyle = "border-[#E0E0E0] text-[#B0B0B0] bg-[#F5F5F5] cursor-not-allowed"; 
                } else if (isSelected) {
                  btnStyle = `${themeColor.border} ${themeColor.text} font-bold` + ' style="background-color: ' + themeColor.bg + ';"'; 
                }

                return (
                  <button
                    key={bank}
                    disabled={isDisabled}
                    onClick={() => toggleBank(bank)}
                    className={`h-[38px] rounded-[8px] border text-[16px] font-medium flex items-center justify-center transition-all ${btnStyle}`}
                  >{bank}
                  </button>
                );
              })}
            </div>
          </div>
        ))}
      </div>

      <div className={`flex items-center justify-center gap-2 border rounded-full px-5 py-2.5 h-[42px] w-full mb-4 ${infoBoxColors.wrapper}`}>
        <div className={`w-[16px] h-[16px] rounded-full flex items-center justify-center text-[10px] font-bold shrink-0 ${infoBoxColors.icon}`}>
          i
        </div>
        <span className={`text-[13px] font-medium tracking-tight ${infoBoxColors.text}`}>
          {infoText}
        </span>
      </div>

      <div 
        className="min-h-[52px] rounded-xl flex items-center justify-between px-5 py-3 flex-wrap gap-2"
        style={{ backgroundColor: themeColor.bg }} 
      >
        {selectedBanks.length === 0 ? (
          <span className="text-[13px] text-[#A5A5A5]">선택된 은행이 없어요</span>
        ) : (
          <div className="flex gap-2 flex-wrap">
            {selectedBanks.map(bank => (
              <span key={bank} className={`flex items-center gap-1 px-3 py-1.5 rounded-full border text-[13px] bg-[#F4FEFD] ${themeColor.border} ${themeColor.text}`}>
                {bank}
                <button onClick={() => toggleBank(bank)} className="ml-1 hover:opacity-70 font-bold">×</button>
              </span>
            ))}
          </div>
        )}
        <button onClick={() => onChange([])} className={`text-[13px] ${themeColor.text} flex items-center gap-1 shrink-0 ml-auto hover:opacity-80`}>
          초기화 ↺
        </button>
      </div>
    </div>
  );
}

// 9. 거래 이력 은행 (최종 페이지 - 1, 2페이지 분할)
export function StepTransaction({ data, setData, cats, onPrev, onSubmit }) {
  const [subStep, setSubStep] = useState(1); 

  const firstBanks = data.firstBanks || [];
  const maturedBanks = data.maturedBanks || [];

  return (
    <StepLayout step={2} title="상세 정보" sub="Y-Fin.만의 정확한 적합도 분석과 예상 수익률 계산을 위해 필요한 정보입니다.">
      
      <div className="flex items-center gap-1.5 mb-6 pl-5">
        <p className="text-[18px] text-[#454545] font-semibold mb-0">거래 이력</p>
      </div>

      <div className="pl-6">
        {/* subStep이 1일 때는 첫 거래 은행만 보여줌 */}
        {subStep === 1 && (
          <BankSelector 
            theme="mint"
            title="첫거래 은행"
            tagText="#첫 거래 자동 활성"
            infoText="복수 선택 가능합니다."
            icontext ="아직 거래해본 적 없는 은행을 선택하면, 해당 
은행의 ‘첫 거래 우대금리'가 자동 반영됩니다."
            cats={cats}
            userRegion={data.region} 
            selectedBanks={firstBanks}
            onChange={(newBanks) => setData({ ...data, firstBanks: newBanks })}
          />
        )}

        {/* subStep이 2일 때는 만기 예적금 은행만 보여줌 */}
        {subStep === 2 && (
          <BankSelector 
            theme="blue"
            title="만기 예적금이 있는 은행"
            tagText="#재 예치 자동 활성"
            infoText="첫 거래 은행으로 선택하지 않은 은행 중에서만 선택 가능합니다."
            icontext="만기된(될) 예 ∙ 적금이 있는 은행을 선택하면 해당 
은행의 ‘재예치 우대금리'가 자동 반영됩니다."
            cats={cats}
            userRegion={data.region} 
            selectedBanks={maturedBanks}
            onChange={(newBanks) => setData({ ...data, maturedBanks: newBanks })}
            disabledBanks={firstBanks} // 앞에서 고른 은행 회색 처리
          />
        )}
      </div>

      <div className="mt-12">
        {subStep === 1 ? (
          <NavButtons 
            onPrev={onPrev} 
            onNext={() => setSubStep(2)} 
          />
        ) : (
          <NavButtons 
            onPrev={() => setSubStep(1)} 
            onSubmit={onSubmit} 
            isLast={true} 
          />
        )}
      </div>
    </StepLayout>
  );
}