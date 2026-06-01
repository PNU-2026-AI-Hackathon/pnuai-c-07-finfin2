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
} from "../components/RecommendSteps";
import { useState } from "react";

export default function Recommend() {
  const { step, formData, setFormData, cats, loading, go, handleSubmit } = useRecommendForm();
  const [isOpen, setIsOpen] = useState(false);

  const steps = [
    <StepSavingPlan      data={formData} setData={setFormData} cats={cats} onNext={go(1)} />,
    <StepBasicInfo       data={formData} setData={setFormData} cats={cats} onPrev={go(0)} onNext={go(2)} />,
    <StepBenefits        data={formData} setData={setFormData} cats={cats} onPrev={go(1)} onNext={go(3)} />,
    <StepPersonalInfo    data={formData} setData={setFormData}             onPrev={go(2)} onNext={go(4)} />,
    <StepRegion          data={formData} setData={setFormData} cats={cats} onPrev={go(3)} onNext={go(5)} />,
    <StepHouseholdIncome data={formData} setData={setFormData} cats={cats} onPrev={go(4)} onNext={go(6)} />,
    <StepHousing         data={formData} setData={setFormData}             onPrev={go(5)} onNext={go(7)} />,
    <StepEmployment      data={formData} setData={setFormData}             onPrev={go(6)} onNext={go(8)} />,
    <StepTransaction     data={formData} setData={setFormData} cats={cats} onPrev={go(7)} onSubmit={handleSubmit} />
  ];

  return (
    <div className="min-h-screen bg-teal-50/40 flex flex-col">
      <div className="flex-1 flex flex-col items-center px-4 pt-16 pb-80">

        {/* 타이틀 */}
        <div className="bg-linear-to-b from-[#EFFFFD] to-[#FFFFFF] text-center mb-8">
          <h1 className="text-[40px] font-bold text-[#4B4B4B] font-gmarket">
            내게 딱 맞는 <span className="text-[#03BFA5]">금융상품,</span><br />
            <span className="text-[#03BFA5]">Y-Fin</span>이 찾아줘요
          </h1>
          <p className="text-[15px] text-[#A5A5A5] mt-6 leading-relaxed tracking-[-0.01em] font-gmarket">
            수백 개 은행 상품을 일일이 비교할 필요 없이, 키워드만 선택하면<br />
            최적의 상품을 찾을 수 있어요.
          </p>
        </div>

        {/* 검색바 + 폼 */}
        <div className="w-full max-w-260 bg-white rounded-4xl shadow-xl">

          {/* 검색바 */}
          <button
            type="button"
            onClick={() => setIsOpen((prev) => !prev)}
            className={`w-full flex items-center justify-between px-6 py-5 hover:bg-gray-50 transition-colors rounded-full border  ${
              isOpen ? "border-transparent" : "border-[#03BFA5]"
            }`}
          >
            {/* 왼쪽 돋보기 아이콘 */}
            <div className="flex items-center gap-3 text-gray-400 pl-2">
              <svg className="w-7 h-7 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
              </svg>
            </div>
            
            {/* 오른쪽 화살표 버튼 */}
            <div className="w-8 h-8 rounded-full bg-[#03BFA5] flex items-center justify-center shrink-0">
              <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </div>
          </button>

          {/* 폼 */}
          {isOpen && (
            <div className="px-14 pb-10 border-t border-gray-100 box-shadow pt-12">
              {!loading && steps[step]}
            </div>
          )}

        </div>
      </div>
    </div>
  );
}